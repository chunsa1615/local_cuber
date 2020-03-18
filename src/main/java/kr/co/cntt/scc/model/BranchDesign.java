package kr.co.cntt.scc.model;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by jslivane on 2016. 4. 19..
 */
@Data
public class BranchDesign {

    private List<Room> rooms;

    private List<Desk> desks;

    public BranchDesign() {
        this.rooms = new ArrayList<>();
        this.desks = new ArrayList<>();

    }

    public BranchDesign(List<Room> rooms, List<Desk> desks) {
        this.rooms = rooms;
        this.desks = desks;

    }

}
