package com.rockbb.jshadow.service.dao;

import com.rockbb.jshadow.service.dto.UserDTO;
import com.rockbb.mocha.commons.Pager;
import com.rockbb.mocha.db.BaseDAO;
import com.rockbb.mocha.db.ClauseGen;
import com.rockbb.mocha.db.DBHelper;
import com.rockbb.mocha.db.EntityParser;

import java.sql.SQLException;
import java.util.List;
import java.util.Map;

public class UserDAO implements BaseDAO<String, UserDTO> {
    private static final String TABLE_NAME = "media_members";
    private static final String[] SORTS = {"member_uid", "member_name", "create_time"};
    private EntityParser parser = new EntityParser(UserDTO.class, "member_uid");

    @Override
    public int insert(UserDTO entity) {
        ClauseGen clause = parser.getInsertClause(entity);
        String sql = "INSERT INTO " + TABLE_NAME + clause.getSql();
        try {
            return DBHelper.update(null, sql, clause.getParams());
        } catch (SQLException e) {
            throw new RuntimeException(e.getMessage());
        }
    }

    @Override
    public int update(UserDTO entity) {
        ClauseGen clause = parser.getUpdateClause(entity).where().and("member_uid = ?", entity.getMemberUid());
        String sql = "UPDATE " + TABLE_NAME + clause.getSql();
        try {
            return DBHelper.update(null, sql, clause.getParams());
        } catch (SQLException e) {
            throw new RuntimeException(e.getMessage());
        }
    }

    @Override
    public int delete(String id) {
        String sql = "DELETE FROM " + TABLE_NAME + " WHERE id = ?";
        try {
            return DBHelper.update(null, sql, new Object[]{id});
        } catch (SQLException e) {
            throw new RuntimeException(e.getMessage());
        }
    }

    @Override
    public UserDTO select(String id) {
        String sql = "SELECT * FROM " + TABLE_NAME + " WHERE id = ?";
        try {
            return DBHelper.select(null, sql, new Object[]{id}, UserDTO.class);
        } catch (SQLException e) {
            throw new RuntimeException(e.getMessage());
        }
    }

    @Override
    public List<UserDTO> list(Pager pager, Map<String, Object> args) {
        ClauseGen clause = where(args).orderby(SORTS[pager.getSort()], pager.getOrder()).limt(pager.getOffset(), pager.getLimit());
        String sql = "SELECT * FROM " + TABLE_NAME + clause.getSql();
        try {
            return DBHelper.list(null, sql, clause.getParams(), UserDTO.class);
        } catch (SQLException e) {
            throw new RuntimeException(e.getMessage());
        }
    }

    @Override
    public long count(Map<String, Object> args) {
        ClauseGen clause = where(args);
        String sql = "SELECT COUNT(1) FROM " + TABLE_NAME + clause.getSql();
        try {
            return DBHelper.count(null, sql, clause.getParams());
        } catch (SQLException e) {
            throw new RuntimeException(e.getMessage());
        }
    }

    private ClauseGen where(Map<String, Object> args) {
        ClauseGen clauseGen = new ClauseGen().where();
        for (String key : args.keySet()) {
            if (key.equals("memberUid")) {
                clauseGen.and("member_uid = ?", args.get(key));
            } else if (key.equals("lastname")) {
                clauseGen.and("lastname LIKE ?", args.get(key));
            } else if (key.equals("memberEmail")) {
                clauseGen.and("member_email LIKE ?", args.get(key));
            } else if (key.equals("cellphone")) {
                clauseGen.and("cellphone LIKE ?", args.get(key));
            } else if (key.equals("createAfter")) {
                clauseGen.and("create_time > ?", args.get(key));
            } else if (key.equals("createBefore")) {
                clauseGen.and("create_time < ?", args.get(key));
            }
        }
        return clauseGen;
    }
}
