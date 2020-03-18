package kr.co.cntt.scc.service;

import kr.co.cntt.scc.Constants;
import kr.co.cntt.scc.model.BranchDesign;
import kr.co.cntt.scc.model.History;
import kr.co.cntt.scc.model.Reservation;
import kr.co.cntt.scc.util.CombinedSqlParameterSource;
import kr.co.cntt.scc.model.Desk;
import kr.co.cntt.scc.model.Room;
import kr.co.cntt.scc.util.DateUtil;
import kr.co.cntt.scc.util.InternalServerError;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by jslivane on 2016. 4. 5..
 */
@Service
@Transactional
@Slf4j
public class BranchDesignService {

    @Autowired
    NamedParameterJdbcTemplate jdbcTemplate;

    @Autowired
    HistoryService historyService;

    public BranchDesignService() {
    }


    /*******************************************************************************/

    /**
     * (캐시 제거) 열람실 리스트
     */
    @CacheEvict(cacheNames = "rooms", allEntries = true)
    public void resetRoomList() { }

    /**
     * 지점 열람실 이름 조회
     * @param branchId
     * @return
     */
    public List<String> selectRoomNameList(String branchId) {
        String s = " SELECT br.name" +
                " FROM branch_room br " +
                " WHERE br.branchId = :branchId " +
                " AND br.useYn = '1' ";

        Map<String, Object> args = new HashMap<>();
        args.put("branchId", branchId);

        return jdbcTemplate.queryForList(s, args, String.class);

    }
    
    /**
     * (캐시) 열람실 리스트 조회
     * @param branchId
     * @return
     * Cache : http://docs.spring.io/spring/docs/current/spring-framework-reference/html/cache.html
     */
    @Cacheable(cacheNames = "rooms", key = "#branchId")
    public List<Room> selectRoomList(String branchId) {
        String s = " SELECT br.id, br.branchId, br.roomId, br.name, br.roomType, br.roomNote, br.t, br.l, br.h, br.w " +
                " FROM branch_room br " +
                " WHERE br.branchId = :branchId AND useYn = :useYn " +
                " ORDER BY br.name ASC ";

        Map<String, Object> args = new HashMap<>();
        args.put("branchId", branchId);
        args.put("useYn", Constants.USE);

        return jdbcTemplate.query(s, args, new BeanPropertyRowMapper<>(Room.class));

    }

    public Room selectRoom(String branchId, String roomId) {

        List<Room> rooms = selectRoomList(branchId);

        for(Room room: rooms) {
            if(roomId.equals(room.getRoomId())) return room;

        }

        return null;

    }


    @CacheEvict(cacheNames = "rooms", key = "#branchId")
    public int insertRoom(String branchId, String roomId, Room room) {
        String s = " INSERT INTO branch_room ( " +
                " branchId, roomId, name, roomType, roomNote, t, l, h, w " +
                " ) VALUES ( " +
                " :branchId, :roomId, :name, :roomType, :roomNote, :t, :l, :h, :w " +
                " ) ";

        CombinedSqlParameterSource source = new CombinedSqlParameterSource(room);
        source.addValue("branchId", branchId);
        source.addValue("roomId", roomId);

        //KeyHolder keyHolder = new GeneratedKeyHolder();
        //return jdbcTemplate.update(s.toString(), source, keyHolder, new String[] {"id"});
        int result = jdbcTemplate.update(s, source);

        if (result == 0) {
            throw new InternalServerError("Failed to create room");

        } else {
            History history = new History(branchId, Constants.HistoryType.ROOM_CREATE, room.toString());
            history.setRoomId(roomId);
            historyService.insertHistory(history);

        }

        return result;

    }

    @CacheEvict(cacheNames = "rooms", key = "#branchId")
    public int updateRoom(String branchId, String roomId, Room room) {
        room.setRoomId(roomId);

        List<Room> rooms = new ArrayList<>();
        rooms.add(room);

        int result = updateRooms(branchId, rooms)[0];

        if (result == 0) {
            throw new InternalServerError("Failed to update room");

        } else {
            History history = new History(branchId, Constants.HistoryType.ROOM_UPDATE, room.toString());
            history.setRoomId(roomId);
            historyService.insertHistory(history);

        }

        return result;

    }


    @CacheEvict(cacheNames = "rooms", key = "#branchId")
    public int[] updateRooms(String branchId, List<Room> rooms) {
        int l = rooms.size();

        if (l > 0) {
            String s = " INSERT INTO branch_room ( " +
                    " branchId, roomId, name, roomType, roomNote, t, l, h, w " +
                    " ) VALUES ( " +
                    " :branchId, :roomId, :name, :roomType, :roomNote, :t, :l, :h, :w " +
                    " ) ON DUPLICATE KEY UPDATE " +
                    " name = :name, roomType = :roomType , t = :t, l = :l, h = :h, w = :w, updateDt = NOW() ";

            // http://www.mkyong.com/spring/spring-named-parameters-examples-in-simplejdbctemplate/
            // http://stackoverflow.com/a/13341287/3614964
            SqlParameterSource[] args = new SqlParameterSource[rooms.size()];
            for (int i = 0; i < l; i++) {
                Room room = rooms.get(i);

                CombinedSqlParameterSource source = new CombinedSqlParameterSource(room);
                source.addValue("branchId", branchId);

                args[i] = source;

            }

            int[] results = jdbcTemplate.batchUpdate(s, args);

            for (int i = 0; i < l; i++) {
                Room room = rooms.get(i);

                if (results[i] == 1) {
                    History history = new History(branchId, Constants.HistoryType.ROOM_CREATE, room.toString());
                    history.setRoomId(room.getRoomId());
                    historyService.insertHistory(history);

                } else if (results[i] == 2) {
                    History history = new History(branchId, Constants.HistoryType.ROOM_UPDATE, room.toString());
                    history.setRoomId(room.getRoomId());
                    historyService.insertHistory(history);

                }

            }

            return results;

        } else {
            return new int[0];

        }

    }

    @CacheEvict(cacheNames = "rooms", key = "#branchId")
    public int deleteRoom(String branchId, String roomId) {
        String s = " UPDATE branch_room SET " +
                " useYn = :useYn, updateDt = NOW() " +
                " WHERE branchId = :branchId AND roomId = :roomId ";

        Map<String, Object> args = new HashMap<>();
        args.put("branchId", branchId);
        args.put("roomId", roomId);
        args.put("useYn", Constants.NOT_USE);

        int result = jdbcTemplate.update(s, args);

        if (result == 0) {
            throw new InternalServerError("Failed to delete room");

        } else {
            History history = new History(branchId, Constants.HistoryType.ROOM_DELETE, "");
            history.setRoomId(roomId);
            historyService.insertHistory(history);

        }

        return result;
    }


    /*******************************************************************************/


    /**
     * (캐시 제거) 좌석 리스트
     */
    @CacheEvict(cacheNames = "desks", allEntries = true)
    public void resetDeskList() { }

    /**
     * 지점좌석 이름 조회
     * @param branchId
     * @return
     */
    public List<String> selectDeskNameList(String branchId) {
        String s = " SELECT bd.name" +
                " FROM branch_desk bd " +
                " WHERE bd.branchId = :branchId " +
                " AND bd.useYn = '1' ";

        Map<String, Object> args = new HashMap<>();
        args.put("branchId", branchId);

        return jdbcTemplate.queryForList(s, args, String.class);

    }
    
    /**
     * (캐시) 좌석 리스트 조회
     * @param branchId
     * @return
     * Cache : http://docs.spring.io/spring/docs/current/spring-framework-reference/html/cache.html
     */
    @Cacheable(cacheNames = "desks", key = "#branchId")
    public List<Desk> selectDeskList(String branchId) {
        String s = " SELECT bd.id, bd.branchId, bd.roomId, bd.deskId, bd.name, bd.deskMax, bd.deskType, bd.t, bd.l, bd.h, bd.w, bd.deskNote" +
                " FROM branch_desk bd " +
                " WHERE bd.branchId = :branchId AND useYn = :useYn " +
                " ORDER BY CAST(bd.name AS unsigned) ASC ";

        Map<String, Object> args = new HashMap<>();
        args.put("branchId", branchId);
        args.put("useYn", Constants.USE);

        return jdbcTemplate.query(s, args, new BeanPropertyRowMapper<>(Desk.class));

    }

    public Desk selectDesk(String branchId, String deskId) {

        List<Desk> desks = selectDeskList(branchId);

        for(Desk desk: desks) {
            if(deskId.equals(desk.getDeskId())) return desk;

        }

        return null;

    }

    public List<Desk> selectDeskList(String branchId, String roomId) {
        List<Desk> desks = selectDeskList(branchId);

        List<Desk> result = new ArrayList<>(0);

        for(Desk desk: desks) {
            if(roomId.equals(desk.getRoomId())) result.add(desk);

        }

        return result;

    }

    public List<Desk> selectNotReservedDeskListByType(String branchId, Constants.DeskType deskType) {
        String s = " SELECT bd.id, bd.branchId, bd.roomId, br.name AS roomName, bd.deskId, bd.name, bd.deskMax, bd.deskType, " +
                " bd.t, bd.l, bd.h, bd.w, bd.deskNote " +
                " FROM branch_desk bd " +
                " JOIN branch_room br ON (br.branchId = bd.branchId AND br.roomId = bd.roomId) " +
                " LEFT JOIN branch_reservation r " +
                " ON ( r.branchId = bd.branchId AND r.deskId = bd.deskId AND r.useYn = :useYn" +
                "       AND :currentDateTime BETWEEN CONCAT(r.deskStartDt, ' ', r.deskStartTm) AND CONCAT(r.deskEndDt, ' ', r.deskEndTm) ) " +
                " WHERE bd.branchId = :branchId AND bd.deskType = :deskType AND bd.useYn = :useYn " +
                " AND r.reservationId IS NULL " +
                " ORDER BY CAST(bd.name AS unsigned) ASC ";

        Map<String, Object> args = new HashMap<>();
        args.put("branchId", branchId);
        args.put("deskType", deskType.getValue());
        args.put("useYn", Constants.USE);
        args.put("currentDateTime", DateUtil.getCurrentDateTimeString());

        return jdbcTemplate.query(s, args, new BeanPropertyRowMapper<>(Desk.class));

    }

    @CacheEvict(cacheNames = "desks", key = "#branchId")
    public int insertDesk(String branchId, String deskId, Desk desk) {
        String s = " INSERT INTO branch_desk ( " +
                " branchId, roomId, deskId, name, deskMax, deskType, t, l, h, w, deskNote " +
                " ) VALUES ( " +
                " :branchId, :roomId, :deskId, :name, :deskMax, :deskType, :t, :l, :h, :w, :deskNote " +
                " ) ";

        CombinedSqlParameterSource source = new CombinedSqlParameterSource(desk);
        source.addValue("branchId", branchId);
        source.addValue("deskId", deskId);

        //KeyHolder keyHolder = new GeneratedKeyHolder();
        //return jdbcTemplate.update(s.toString(), source, keyHolder, new String[] {"id"});
        int result = jdbcTemplate.update(s, source);

        if (result == 0) {
            throw new InternalServerError("Failed to create desk");

        } else {
            History history = new History(branchId, Constants.HistoryType.DESK_CREATE, desk.toString());
            history.setRoomId(desk.getRoomId());
            history.setDeskId(deskId);
            historyService.insertHistory(history);

        }

        return result;

    }

    @CacheEvict(cacheNames = "desks", key = "#branchId")
    public int updateDesk(String branchId, String deskId, Desk desk) {
        desk.setDeskId(deskId);

        List<Desk> desks = new ArrayList<>();
        desks.add(desk);

        int result = updateDesks(branchId, desks)[0];

        if (result == 0) {
            throw new InternalServerError("Failed to update desk");

        } else {
            History history = new History(branchId, Constants.HistoryType.DESK_UPDATE, desk.toString());
            history.setRoomId(desk.getRoomId());
            history.setDeskId(deskId);
            historyService.insertHistory(history);

        }


        return result;


    }

    @CacheEvict(cacheNames = "desks", key = "#branchId")
    public int[] updateDesks(String branchId, List<Desk> desks) {
        int l = desks.size();

        if (l > 0) {
            String s = " INSERT INTO branch_desk ( " +
                    " branchId, roomId, deskId, name, deskMax, deskType, t, l, h, w, deskNote " +
                    " ) VALUES ( " +
                    " :branchId, :roomId, :deskId, :name, :deskMax, :deskType, :t, :l, :h, :w, :deskNote " +
                    " ) ON DUPLICATE KEY UPDATE " +
                    " roomId = :roomId, name = :name, deskMax = :deskMax, deskType = :deskType, " +
                    " t = :t, l = :l, h = :h, w = :w, deskNote = :deskNote, updateDt = NOW() ";

            // http://www.mkyong.com/spring/spring-named-parameters-examples-in-simplejdbctemplate/
            // http://stackoverflow.com/a/13341287/3614964
            SqlParameterSource[] args = new SqlParameterSource[desks.size()];
            for (int i = 0; i < l; i++) {
                Desk desk = desks.get(i);

                CombinedSqlParameterSource source = new CombinedSqlParameterSource(desk);
                source.addValue("branchId", branchId);

                args[i] = source;

            }

            int[] results =  jdbcTemplate.batchUpdate(s, args);

            for (int i = 0; i < l; i++) {
                Desk desk = desks.get(i);

                if (results[i] == 1) {
                    History history = new History(branchId, Constants.HistoryType.DESK_CREATE, desk.toString());
                    history.setRoomId(desk.getRoomId());
                    history.setDeskId(desk.getDeskId());
                    historyService.insertHistory(history);

                } else if (results[i] == 2) {
                    History history = new History(branchId, Constants.HistoryType.DESK_UPDATE, desk.toString());
                    history.setRoomId(desk.getRoomId());
                    history.setDeskId(desk.getDeskId());
                    historyService.insertHistory(history);

                }

            }

            return results;


        } else {
            return new int[0];

        }
    }

    @CacheEvict(cacheNames = "desks", key = "#branchId")
    public int deleteDesk(String branchId, String deskId) {
        String s = " UPDATE branch_desk SET " +
                " useYn = :useYn, updateDt = NOW() " +
                " WHERE branchId = :branchId AND deskId = :deskId ";

        Map<String, Object> args = new HashMap<>();
        args.put("branchId", branchId);
        args.put("deskId", deskId);
        args.put("useYn", Constants.NOT_USE);

        int result = jdbcTemplate.update(s, args);

        if (result == 0) {
            throw new InternalServerError("Failed to delete desk");

        } else {
            History history = new History(branchId, Constants.HistoryType.DESK_DELETE, "");
            history.setDeskId(deskId);
            historyService.insertHistory(history);

        }

        return result;
    }


    /*******************************************************************************/


    @Transactional
    public BranchDesign findByBranchId(String branchId) {
        List<Room> rooms = selectRoomList(branchId);

        List<Desk> desks = selectDeskList(branchId);

        return new BranchDesign(rooms, desks);

    }

    @Transactional
    public void updateByBranchId(String branchId, BranchDesign branchDesign) {
        updateRooms(branchId, branchDesign.getRooms());

        updateDesks(branchId, branchDesign.getDesks());


    }
    
    /******************************************
     * 
     * 홈화면 통계
     * 
     * *************************************/
    
    public Integer selectDeskCountList(String branchId) {
        String s = " SELECT sum(deskMax) " +
                " FROM branch_desk d " +
                " WHERE 1=1 " +
                " AND d.branchId = :branchId " +
                " AND (roomId != null or roomId != '') " +
                " AND d.useYn = :useYn ";
        
        Map<String, Object> args = new HashMap<>();
        args.put("branchId", branchId);
        
        args.put("useYn", Constants.USE);

        
        //return jdbcTemplate.query(s, args, new BeanPropertyRowMapper<>(Desk.class));
        return jdbcTemplate.queryForObject(s, args, Integer.class);

    }
}
