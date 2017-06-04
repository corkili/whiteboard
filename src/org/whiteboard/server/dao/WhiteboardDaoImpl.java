package org.whiteboard.server.dao;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;
import org.whiteboard.server.model.Whiteboard;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by 李浩然 on 2017/5/15.
 */
@Repository
public class WhiteboardDaoImpl implements WhiteboardDao {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    private Logger logger = Logger.getLogger(WhiteboardDaoImpl.class);

    @Override
    public Whiteboard findBoardById(long boardId) {
        Whiteboard whiteboard = null;
        String sql = getSqlForSelectBoard(COL_BOARD_ID);
        try {
            RowMapper<Whiteboard> rowMapper = BeanPropertyRowMapper.newInstance(Whiteboard.class);
            whiteboard = jdbcTemplate.queryForObject(sql, new Object[]{ boardId }, rowMapper);
            if (whiteboard != null) {
                whiteboard.setBoardContent(getContent(whiteboard.getBoardContent()));
            }
        } catch (Exception e) {
            // do nothing
        }
        return whiteboard;
    }

    @Override
    public List<Whiteboard> findBoardsByMeetingId(long meetingId) {
        List<Whiteboard> whiteboards = null;
        String sql = getSqlForSelectBoard(COL_MEETING_ID);
        try {
            RowMapper<Whiteboard> rowMapper = BeanPropertyRowMapper.newInstance(Whiteboard.class);
            whiteboards = jdbcTemplate.query(sql, new Object[]{ meetingId }, rowMapper);
            if (whiteboards != null) {
                for (Whiteboard whiteboard : whiteboards) {
                    whiteboard.setBoardContent(getContent(whiteboard.getBoardContent()));
                }
            }
        } catch (Exception e) {
            // do nothing
        }
        if (whiteboards == null) {
            whiteboards = new ArrayList<>();
        }
        return whiteboards;
    }

    @Override
    public Whiteboard addBoardToDB(Whiteboard whiteboard) {
        Whiteboard result = null;
        try {
            System.out.println(getSqlForInsertBoard());
            jdbcTemplate.update(getSqlForInsertBoard(), getParamsForInsertBoard(whiteboard));
            RowMapper<Whiteboard> rowMapper = BeanPropertyRowMapper.newInstance(Whiteboard.class);
            saveContent(whiteboard);
            result = jdbcTemplate.queryForObject(getSqlForSelectBoard(COL_MEETING_ID, COL_BOARD_NAME),
                    new Object[]{ whiteboard.getMeetingId(), whiteboard.getBoardName() }, rowMapper);
            if (result != null) {
                result.setBoardContent(whiteboard.getBoardContent());
            }
        } catch (Exception e) {
            // do nothing
        }
        return result;
    }

    @Override
    public boolean addBoardsToDB(List<Whiteboard> whiteboards) {
        try {
            List<Object[]> params = new ArrayList<>();
            for(Whiteboard whiteboard : whiteboards) {
                params.add(getParamsForInsertBoard(whiteboard));
            }
            System.out.println(getSqlForInsertBoard() +  ": " + params.size());
            jdbcTemplate.batchUpdate(getSqlForInsertBoard(), params);
            for (Whiteboard whiteboard : whiteboards) {
                saveContent(whiteboard);
            }
            return true;
        } catch (Exception e) {
            // do nothing
        }
        return false;
    }

    @Override
    public Whiteboard updateBoard(Whiteboard whiteboard) {
        try {
            int count = jdbcTemplate.update(getSqlForUpdateBoard(), getParamsForUpdateBoard(whiteboard));
            if (count == 1) {
                return whiteboard;
            } else {
                return null;
            }
        } catch (Exception e) {
            // do nothing
        }
        return null;
    }

    @Override
    public Whiteboard deleteBoard(Whiteboard whiteboard) {
        try {
            int count = jdbcTemplate.update(getSqlForDeleteBoard(COL_BOARD_ID), whiteboard.getBoardId());
            if (count == 1) {
                return whiteboard;
            } else {
                return null;
            }
        } catch (Exception e) {
            // do nothing
        }
        return null;
    }

    @Override
    public int deleteBoards(long meetingId) {
        try {
            return jdbcTemplate.update(getSqlForDeleteBoard(COL_MEETING_ID), meetingId);
        } catch (Exception e) {
            // do nothing
        }
        return 0;
    }

    private String getSqlForSelectBoard(String col) {
        return "select * from " + TABLE_WHITEBOARD + " where " + col + "=?";
    }

    private String getSqlForSelectBoard(String col1, String col2) {
        return "select * from " + TABLE_WHITEBOARD + " where " + col1 + "=? and " + col2 + "=?";
    }

    private String getSqlForInsertBoard() {
        return "insert into " + TABLE_WHITEBOARD + " (" + COL_BOARD_CONTENT + ","
                + COL_MEETING_ID + "," + COL_BOARD_NAME + ") values(?,?,?)";
    }

    private Object[] getParamsForInsertBoard(Whiteboard whiteboard) {
        String contentPath = getPathForBoard(whiteboard);
        return new Object[] { contentPath, whiteboard.getMeetingId(), whiteboard.getBoardName() };
    }

    private String getSqlForUpdateBoard() {
        return "update " + TABLE_WHITEBOARD + " set " + COL_BOARD_NAME + "=?,"
                + COL_BOARD_CONTENT + "=?," + COL_MEETING_ID + "=? where "
                + COL_BOARD_ID + "=?";
    }

    private Object[] getParamsForUpdateBoard(Whiteboard whiteboard) {
        return new Object[] { whiteboard.getBoardContent(), whiteboard.getMeetingId(),
                whiteboard.getBoardName(), whiteboard.getBoardId() };
    }

    private String getSqlForDeleteBoard(String col) {
        return "delete from " + TABLE_WHITEBOARD + " where " + col + "=?";
    }

    private String getPathForBoard(Whiteboard whiteboard) {
        return "board/" + whiteboard.getMeetingId() + "_" + whiteboard.getBoardName() + ".wbd";
    }

    private void saveContent(Whiteboard whiteboard) {
        File dir = new File("board");
        boolean flag = true;
        if (!dir.exists()) {
            flag = dir.mkdirs();
        }
        if (flag) {
            String path = getPathForBoard(whiteboard);
            try {
                BufferedWriter out = new BufferedWriter(new FileWriter(path));
                out.write(whiteboard.getBoardContent());
                logger.info(whiteboard.getBoardContent().substring(0, 10) + " : " + whiteboard.getBoardContent().length());
                out.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private String getContent(String path) {
        StringBuilder result = new StringBuilder();
        try {
            BufferedReader in = new BufferedReader(new FileReader(path));
            String str;
            while ((str = in.readLine()) != null) {
                result.append(str);
            }
            in.close();
            return result.toString();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return result.toString();
    }
}
