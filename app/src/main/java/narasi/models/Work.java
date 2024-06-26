package narasi.models;


import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;


public class Work {
    private int id;
    private String title;
    private String content;
    private int pageMarker;
    private String tags;
    private List<Comment> comments;
    private int kudosCount;
    private int userId;
    private String authorFullName;
    private boolean isDraft;
    private Timestamp timestamp;


    public Work() {
        this.id = 0;
        this.title = "";
        this.content = "";
        this.pageMarker = 1; 
        this.tags = "";
        this.comments = new ArrayList<>();
        this.kudosCount = 0;
        this.userId = 0;
        this.authorFullName = "";
        this.isDraft = true;
        this.timestamp = new Timestamp(System.currentTimeMillis());
    }


    public Work(int id, String title, String content, int pageMarker, String tags, int kudosCount, int userId, String authorFullName, boolean isDraft, Timestamp timestamp) {
        this.id = id;
        this.title = title;
        this.content = content;
        this.pageMarker = pageMarker;
        this.tags = tags;
        this.comments = new ArrayList<>();
        this.kudosCount = kudosCount;
        this.userId = userId;
        this.authorFullName = authorFullName;
        this.isDraft = isDraft;
        this.timestamp = timestamp;
    }


    public int getId() {
        return id;
    }


    public void setId(int id) {
        this.id = id;
    }


    public String getTitle() {
        return title;
    }


    public void setTitle(String title) {
        this.title = title;
    }


    public String getContent() {
        return content;
    }


    public void setContent(String content) {
        this.content = content;
    }


    public int getPageMarker() {
        return pageMarker;
    }


    public void setPageMarker(int pageMarker) {
        this.pageMarker = pageMarker;
    }


    public String getTags() {
        return tags;
    }


    public void setTags(String tags) {
        this.tags = tags;
    }


    public List<Comment> getComments() {
        return comments;
    }


    public void setComments(List<Comment> comments) {
        this.comments = comments;
    }


    public int getKudosCount() {
        return kudosCount;
    }


    public void setKudosCount(int kudosCount) {
        this.kudosCount = kudosCount;
    }


    public int getUserId() {
        return userId;
    }


    public void setUserId(int userId) {
        this.userId = userId;
    }


    public String getAuthorFullName() {
        return authorFullName;
    }


    public void setAuthorFullName(String authorFullName) {
        this.authorFullName = authorFullName;
    }


    public boolean isDraft() {
        return isDraft;
    }


    public void setDraft(boolean draft) {
        isDraft = draft;
    }


    public Timestamp getTimestamp() {
        return timestamp;
    }


    public void setTimestamp(Timestamp timestamp) {
        this.timestamp = timestamp;
    }
}





