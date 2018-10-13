package Obstacle;

import com.simulation.Potential.PotentialCell;
import com.simulation.Potential.PotentialCells;

import javax.vecmath.Vector2f;
import java.util.ArrayList;

public class Obstacle {
    PotentialCells potentialCells;
    ArrayList<PotentialCell> obstacleCell;

    public Obstacle(PotentialCells potentialCells) {
        this.potentialCells = potentialCells;
        obstacleCell = new ArrayList<>();
    }

    public Obstacle(PotentialCells potentialCells, int x, int y, int w, int h){
        this.potentialCells = potentialCells;
        this.obstacleCell = new ArrayList<>();
        setShapeObstacle(x,y,w,h);
    }

    public void setShapeObstacle(Vector2f pos, int w, int h) {
        PotentialCell startPoint = potentialCells.getPotentialCell(pos);
        PotentialCell diagonalPoint = potentialCells.getPotentialCell(new Vector2f(pos.x + w, pos.y + h));
        Vector2f startMatrixNumber = potentialCells.getMatrixNumber(startPoint);
        Vector2f diagonalMatrixNumber = potentialCells.getMatrixNumber(diagonalPoint);
        ArrayList<ArrayList<PotentialCell>> matrixCell = potentialCells.getMatrixPotentialCells();

        for (int i = (int) startMatrixNumber.x; i < (int) diagonalMatrixNumber.x; i++) {
            for (int j = (int) startMatrixNumber.y; j < (int) diagonalMatrixNumber.y; j++) {
                obstacleCell.add(matrixCell.get(i).get(j));
            }
        }
        setPotential();
    }

    public void setShapeObstacle(int x, int y, int w, int h) {
        setShapeObstacle(new Vector2f(x, y), w, h);
    }

    public void setShapeCircle(Vector2f pos, int w, int h) {

    }

    public void setPotential(){
        potentialCells.setObstacle(this);
    }

    public PotentialCells getPotentialCells() {
        return potentialCells;
    }

    public ArrayList<PotentialCell> getObstacleCell() {
        return obstacleCell;
    }
}