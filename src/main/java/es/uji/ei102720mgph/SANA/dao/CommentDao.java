package es.uji.ei102720mgph.SANA.dao;

import es.uji.ei102720mgph.SANA.model.Comment;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Formatter;
import java.util.List;

@Repository
public class CommentDao {

    private JdbcTemplate jdbcTemplate;

    @Autowired
    public void setDataSource(DataSource dataSource) {
        jdbcTemplate = new JdbcTemplate(dataSource);
    }

    public void addComment(Comment comment){
        boolean excepcion;
        Formatter fmt;
        do {
            try {
                fmt = new Formatter();
                jdbcTemplate.update("INSERT INTO Comment VALUES(?, ?, ?, ?, ?, ?)",
                        "" + fmt.format("%06d", Comment.getContador()), comment.getCommentBody(), comment.getScore(),
                        LocalDate.now(), comment.getCitizenEmail(), comment.getNaturalArea());
                excepcion = false;
            } catch (DuplicateKeyException e) {
                excepcion = true;
            } finally {
                Comment.incrementaContador();
            }
        } while (excepcion);
    }

    public void deleteComment(Comment comment){
        jdbcTemplate.update("DELETE FROM Comment WHERE commentId = ?",
                comment.getCommentId());
    }

    public void deleteComment(String commentId){
        jdbcTemplate.update("DELETE FROM Comment WHERE commentId = ?",
                commentId);
    }

    public void updateComment(Comment comment){
        jdbcTemplate.update("UPDATE Comment " +
                "SET commentBody = ?, score = ?, date = ?, " +
                "citizenEmail = ?, naturalArea = ? " +
                "WHERE commentId = ?",
                comment.getCommentBody(), comment.getScore(), comment.getDate(),
                comment.getCitizenEmail(), comment.getNaturalArea(),
                comment.getCommentId());
    }

    public Comment getComment(String commentId) {
        try {
            return jdbcTemplate.queryForObject("SELECT * FROM Comment WHERE commentId = ?",
                    new CommentRowMapper(),
                    commentId);
        }
        catch(EmptyResultDataAccessException e) {
            return null;
        }
    }

    public List<Comment> getCommentsOfNaturalArea(String naturalArea) {
        try {
            return jdbcTemplate.query("SELECT * FROM Comment WHERE naturalArea = ?",
                    new CommentRowMapper(),
                    naturalArea);
        }
        catch(EmptyResultDataAccessException e) {
            return new ArrayList<Comment>();
        }
    }

    public List<Comment> getComments() {
        try {
            return jdbcTemplate.query("SELECT * FROM Comment",
                    new CommentRowMapper());
        }
        catch(EmptyResultDataAccessException e) {
            return new ArrayList<Comment>();
        }
    }
}
