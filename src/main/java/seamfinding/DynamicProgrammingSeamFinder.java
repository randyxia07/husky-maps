package seamfinding;

import seamfinding.energy.EnergyFunction;

import java.util.ArrayList;
import java.util.List;

/**
 * Dynamic programming implementation of the {@link SeamFinder} interface.
 *
 * @see SeamFinder
 */
public class DynamicProgrammingSeamFinder implements SeamFinder {

    @Override
    public List<Integer> findHorizontal(Picture picture, EnergyFunction f) {
        // TODO: Replace with your code
        // throw new UnsupportedOperationException("Not implemented yet");
        int width = picture.width();
        int height = picture.height();
 
        // cost[x][y] = minimum total energy of a seam from column 0 to pixel (x, y)
        double[][] cost = new double[width][height];
 
        // Base case: leftmost column costs are just the energy of each pixel
        for (int y = 0; y < height; y++) {
            cost[0][y] = f.apply(picture, 0, y);
        }

        // Fill left to right: cost[x][y] = energy(x,y) + min of cost[x-1][y-1], cost[x-1][y], cost[x-1][y+1]
        for (int x = 1; x < width; x++) {
            for (int y = 0; y < height; y++) {
                double best = cost[x - 1][y];
                if (y > 0) {
                    best = Math.min(best, cost[x - 1][y - 1]);
                }
                if (y < height - 1) {
                    best = Math.min(best, cost[x - 1][y + 1]);
                }
                cost[x][y] = f.apply(picture, x, y) + best;
            }
        }

        // Find the y index with minimum cost in the rightmost column
        int minY = 0;
        for (int y = 1; y < height; y++) {
            if (cost[width - 1][y] < cost[width - 1][minY]) {
                minY = y;
            }
        }

        // Trace back from right to left to recover the seam
        List<Integer> seam = new ArrayList<>(width);
        // Fill with placeholders so we can set by index
        for (int x = 0; x < width; x++) {
            seam.add(0);
        }
        seam.set(width - 1, minY);
 
        for (int x = width - 2; x >= 0; x--) {
            int prevY = seam.get(x + 1);
            int bestY = prevY;
            if (prevY > 0 && cost[x][prevY - 1] < cost[x][bestY]) {
                bestY = prevY - 1;
            }
            if (prevY < height - 1 && cost[x][prevY + 1] < cost[x][bestY]) {
                bestY = prevY + 1;
            }
            seam.set(x, bestY);
        }

        return seam;
    }


}
