package uk.co.davidkanekanian.fabrik;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.room.Room;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.google.android.material.snackbar.Snackbar;

import org.joml.Vector2f;

import java.util.ArrayList;
import java.util.List;

import uk.co.davidkanekanian.fabrik.math.FabrikSolver;
import uk.co.davidkanekanian.fabrik.persistence.Chain;
import uk.co.davidkanekanian.fabrik.persistence.ChainDao;
import uk.co.davidkanekanian.fabrik.persistence.ChainDatabase;
import uk.co.davidkanekanian.fabrik.persistence.Point;

public class MainActivity extends AppCompatActivity implements View.OnTouchListener {
    /** Canvas to draw world onto. */
    private Canvas canvas;

    /** Drop dragged point here to delete it. */
    private ImageView deletePointImage;

    /** Point that is being dragged, or  -1 if not being dragged. */
    private int dragPointContext = -1;

    /** ALl active points in the world. */
    private List<Vector2f> points = new ArrayList<>();

    /** Previously created points in the world. */
    private List<Vector2f> lastPoints = new ArrayList<>();

    /** Previously saved points that got overwritten. */
    private List<Vector2f> overwrittenSavedPoints = new ArrayList<>();

    /** Screen distance to point to grab it, in pixels. */
    private static final float maxGrabDist = 100.f;

    /** Whether the points are in locked mode. */
    private boolean isLocked = false;

    /** Location of the end effector. */
    private Vector2f endEffector = new Vector2f(100.f, 100.f);

    /** Solver of IK system. */
    private FabrikSolver fabrikSolver = new FabrikSolver();

    /** ID of chain in database, or -1 if there is no save context. */
    private int chainId = -1;

    /** Database for use in the app */
    public static ChainDatabase database;

    /** Code for opening a chain */
    private static final int REQ_CODE_BROWSE_CHAIN = 1001;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        database = Room.databaseBuilder(getApplicationContext(), ChainDatabase.class, "chains")
                .allowMainThreadQueries()
                .build();

        canvas = findViewById(R.id.fabrik_canvas);
        canvas.setOnTouchListener(this);
        canvas.master = this;

        deletePointImage = findViewById(R.id.delete_point_image);
        refreshDeletePointImage();

        final ImageView moreActionsButton = findViewById(R.id.more_actions_button);
        final LinearLayout moreActionsMenu = findViewById(R.id.more_actions_menu);

        // Hide more actions menu by default.
        moreActionsMenu.setVisibility(View.GONE);

        // Toggle more actions menu visibility when more action button clicked.
        moreActionsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                switch (moreActionsMenu.getVisibility()) {
                    case View.VISIBLE: moreActionsMenu.setVisibility(View.GONE); break;
                    case View.GONE: moreActionsMenu.setVisibility(View.VISIBLE); break;
                    case View.INVISIBLE: throw new Error("Invalid visibility state for moreActionMenu");
                }
            }
        });

        final ImageView lockButton = findViewById(R.id.lock_button);
        lockButton.setOnClickListener(new View.OnClickListener() {
            @Override public void onClick(View view) { onClickLock(view); }
        });

        final ImageView saveButton = findViewById(R.id.save_button);
        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override public void onClick(View view) { onClickSave(view); }
        });

        final ImageView browseButton = findViewById(R.id.browse_button);
        browseButton.setOnClickListener(new View.OnClickListener() {
            @Override public void onClick(View view) { onClickBrowse(view); }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQ_CODE_BROWSE_CHAIN && data != null) {
            // Save in the object for later saving, possibly.
            chainId = data.getIntExtra("chainId", -1);
            Log.d("david", "loading chain id " + chainId);
            loadChain(chainId);
        }
    }

    /** Hide the delete button unless drag in operation. */
    private void refreshDeletePointImage() {
        deletePointImage.setVisibility(
                dragPointContext != -1 && !isLocked
                ? View.VISIBLE : View.INVISIBLE);
    }

    private void onClickLock(View view) {
        isLocked = !isLocked;
        if (isLocked) {
            // When from editing to locked.
            // Deep clone all points, not just their references.
            lastPoints.clear();
            for (Vector2f p : points) { lastPoints.add(new Vector2f(p)); }
        } else {
            // Went from locked to editing mode.
            // Restore the points before editing.
            points.clear();
            points.addAll(lastPoints);
        }
        canvas.invalidate();
    }

    private void onClickSave(View view) {
        if (chainId == -1) {
            saveNewChain(view);
        } else {
            saveOverChain(view);
        }
    }

    /** Save over the current file. */
    private void saveOverChain(View view) {
        if (chainId == -1) {
            // No active chain to save over.
            return;
        }

        // Save a copy of the old saved points in the points in case of undo.
        loadChainToList(chainId, overwrittenSavedPoints);
        saveOverChain(chainId, points);

        // add an undo button in case user didn't want to overwrite
        Chain c = database.chainDao().getChain(chainId);
        final String savedText = String.format("Saved chain as %s", c.name);
        Snackbar.make(view, savedText, Snackbar.LENGTH_LONG)
                .setAction("Undo", new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        saveOverChain(chainId, overwrittenSavedPoints);
                        // Keep the working set of points as-is.
                    }
                })
                .show();
    }

    /** Unlink old chain points and write new points. */
    private void saveOverChain(int chainId, List<Vector2f> points) {
        final ChainDao dao = database.chainDao();

        // First remove all old points.
        final Point[] oldPoints = dao.getPointsInChain(chainId);
        // Remove links to chains before we can delete points.
        dao.removeAllPointsFromChain(chainId);
        for (final Point p : oldPoints) {
            dao.deletePoint(p.id);
        }

        // Add new points and link to old chain.
        for (final Vector2f p : points) {
            long pId = dao.addPoint(p.x, p.y);
            dao.addPointToChain(chainId, (int) pId);
        }
    }

    /** Create a new file. */
    private void saveNewChain(View view) {
        // First time saving. Create new file.
        View dialogView = getLayoutInflater().inflate(R.layout.dialog_save_name, null);
        final EditText editText = dialogView.findViewById(R.id.chain_name_input);

        final AlertDialog saveDialog = new AlertDialog.Builder(this)
                .setTitle("Save Chain")
                .setMessage("Enter a name to save the chain")
                .setView(dialogView)
                .create();

        // Bind actions to click and cancel dialog.
        dialogView.findViewById(R.id.dialog_save_name_save).setOnClickListener(
                new View.OnClickListener() {
                    @Override public void onClick(View view) {
                        saveChain(editText.getText().toString());
                        saveDialog.dismiss();
                    }
                }
        );
        dialogView.findViewById(R.id.dialog_save_name_cancel).setOnClickListener(
                new View.OnClickListener() {
                    @Override public void onClick(View view) {
                        saveDialog.dismiss();
                    }
                }
        );

        // Show the dialog.
        saveDialog.show();
    }

    private void onClickBrowse(View view) {
        // Open the chain viewing activity.
        Intent intent = new Intent(view.getContext(), BrowseChains.class);
        startActivityForResult(intent, REQ_CODE_BROWSE_CHAIN);
    }

    private void saveChain(String name) {
        final ChainDao dao = database.chainDao();
        final long newChainId = dao.addChain(name);
        for (Vector2f p : lastPoints) {
            final long pointId = dao.addPoint(p.x, p.y);
            dao.addPointToChain((int) newChainId, (int) pointId);
        }

        // Save in the member field in case of saving over the file.
        chainId = (int) newChainId;
    }

    private void loadChain(int inChainId) {
        points.clear();
        lastPoints.clear();
        loadChainToList(inChainId, points);

        // Set the saved chain ID to the active chain.
        chainId = inChainId;
    }

    private void loadChainToList(int chainId, List<Vector2f> outList) {
        outList.clear();
        Point[] pointsToLoad = database.chainDao().getPointsInChain(chainId);
        for (Point p : pointsToLoad) {
            outList.add(new Vector2f(p.x, p.y));
        }
    }

    @Override
    public boolean onTouch(View view, MotionEvent motionEvent) {
        switch (motionEvent.getAction() & MotionEvent.ACTION_MASK) {
            case MotionEvent.ACTION_DOWN:
                canvas.isDown = true;
                if (!tryGrabPoint(motionEvent)) {
                    addPointAndGrab(motionEvent);
                }
                refreshDeletePointImage();
                break;
            case MotionEvent.ACTION_UP:
                canvas.isDown = false;
                tryDeleteGrabbedPoint();
                // Invalidate dragged point regardless of whether it was deleted.
                invalidateDragPointContext();
                refreshDeletePointImage();
                break;
            case MotionEvent.ACTION_MOVE:
                moveGrabbedPoint(motionEvent);
                break;
        }
        // Redraw canvas.
        canvas.invalidate();
        return true;  // Event is consumed.
    }

    /** Make drag context invalid. */
    private void invalidateDragPointContext() {
        dragPointContext = -1;
    }

    /** Delete the dragged point if released over the delete image.
     *
     * Naturally this invalidates drag context if the point was deleted.
     *
     * @return Whether a point was deleted. */
    private boolean tryDeleteGrabbedPoint() {
        if (dragPointContext != -1) {
            Vector2f point = points.get(dragPointContext);
//                    if (deletePointImage.getClipBounds().contains((int)point.x, (int)point.y)) {
            float w = deletePointImage.getWidth(),
                    h = deletePointImage.getHeight(),
                    x = deletePointImage.getX() + ((LinearLayout)deletePointImage.getParent()).getX(),
                    y = deletePointImage.getY() + ((LinearLayout)deletePointImage.getParent()).getY();
            if (x < point.x && point.x < x + w &&
                    y < point.y && point.y < y + h) {
                // Delete that point.
                points.remove(dragPointContext);
                invalidateDragPointContext();
                return true;
            }
        }
        // Point was not deleted.
        return false;
    }

    /** Try to grab a point under the finger. */
    private boolean tryGrabPoint(MotionEvent motionEvent) {
        if (isLocked) {
            // Snap end effector to finger in any point when locked.
            dragPointContext = 0;
            return true;
        }
        for (int i = 0; i < points.size(); i++) {
            Vector2f point = points.get(i);
            if (isWithinRange(point, motionEvent)) {
                // The point can be dragged!
                dragPointContext = i;
                return true;
            }
        }
        return false;
    }

    private boolean isWithinRange(Vector2f point, MotionEvent motionEvent) {
        return point.distance(motionEvent.getX(), motionEvent.getY()) < maxGrabDist;
    }

    /** Add a new point at the touch point. */
    private void addPointAndGrab(MotionEvent motionEvent) {
        if (!isLocked) {
            points.add(new Vector2f(motionEvent.getX(), motionEvent.getY()));
            // Set the context so touch move events can drag this point.
            dragPointContext = points.size() - 1;
        }
    }

    /** Move the grabbed point, if any, based on a touch event. */
    private void moveGrabbedPoint(MotionEvent motionEvent) {
        canvas.fingerLocation.set(motionEvent.getX(), motionEvent.getY());
        if (dragPointContext != -1) {
            final Vector2f point = isLocked ? endEffector : points.get(dragPointContext);
            point.set(motionEvent.getX(), motionEvent.getY());
            if (isLocked) {
                // Update fabrik solver.
                fabrikSolver.configure(points);
                fabrikSolver.solve(new Vector2f(endEffector));
            }
        }
    }

    public int getDragPointContext() {
        return dragPointContext;
    }

    public List<Vector2f> getPoints() {
        return points;
    }

    public boolean isLocked() {
        return isLocked;
    }

    public Vector2f getEndEffector() {
        return endEffector;
    }
}