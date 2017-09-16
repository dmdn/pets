package by.ddv.zoo.models;


import java.util.List;
import io.realm.RealmList;
import io.realm.RealmObject;
import io.realm.annotations.Ignore;



public class Pet extends RealmObject {

    private long id;

    private Category category;
    private String name;

    @Ignore
    private List<String> photoUrls;

    //transient - ignore, mark field it will be excluded

    private RealmList<RealmString> photoUrlsRealm;

    @Ignore
    private List<Tag> tags;

    private RealmList<Tag> tagsRealm;

    private String status;

    //flag changes in the data locally
    // from_net / created_by_me / changed_by_me / deleted_by_me
    private String change;


    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public Category getCategory() {
        return category;
    }

    public void setCategory(Category category) {
        this.category = category;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<String> getPhotoUrls() {
        return photoUrls;
    }

    public void setPhotoUrls(List<String> photoUrls) {
        this.photoUrls = photoUrls;
    }

    public RealmList<RealmString> getPhotoUrlsRealm() {
        return photoUrlsRealm;
    }

    public void setPhotoUrlsRealm(RealmList<RealmString> photoUrlsRealm) {
        this.photoUrlsRealm = photoUrlsRealm;
    }

    public List<Tag> getTags() {
        return tags;
    }

    public void setTags(List<Tag> tags) {
        this.tags = tags;
    }

    public RealmList<Tag> getTagsRealm() {
        return tagsRealm;
    }

    public void setTagsRealm(RealmList<Tag> tagsRealm) {
        this.tagsRealm = tagsRealm;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getChange() {
        return change;
    }

    public void setChange(String change) {
        this.change = change;
    }


}

