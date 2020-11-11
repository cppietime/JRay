package com.funguscow.rc3d.world;

import com.funguscow.rc3d.physics.Vector2D;
import com.funguscow.rc3d.world.cell.Cell;

public class World {

    public Cell[] cells;

    public World(){
        cells = new Cell[10000];
        for(int i = 0; i < 10000; i++){
            if(i % 31 == 0)
                cells[i] = Cell.BLUE;
            else if(i % 71 == 1)
                cells[i] = Cell.RED;
            else if(i % 9 == 2)
                cells[i] = Cell.GREEN;
            else
                cells[i] = Cell.EMPTY;
        }
    }

    public Cell getCell(Vector2D position){
        int index = (int) (position.y * 100 + position.x);
        if(index < 0 || index >= cells.length)
            return Cell.EMPTY;
        return cells[index];
    }

}
