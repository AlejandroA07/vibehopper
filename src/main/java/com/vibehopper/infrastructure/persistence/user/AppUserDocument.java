package com.vibehopper.infrastructure.persistence.user;
import com.vibehopper.domain.user.Role; import org.springframework.data.annotation.Id; import org.springframework.data.mongodb.core.index.Indexed; import org.springframework.data.mongodb.core.mapping.Document;
@Document(collection="users") public class AppUserDocument {
    @Id private String id; private String name; @Indexed(unique=true) private String email; private String password; private Role role; private boolean locked; private boolean enabled;
    public String getId(){return id;} public void setId(String id){this.id=id;} public String getName(){return name;} public void setName(String name){this.name=name;} public String getEmail(){return email;} public void setEmail(String email){this.email=email;} public String getPassword(){return password;} public void setPassword(String password){this.password=password;} public Role getRole(){return role;} public void setRole(Role role){this.role=role;} public boolean isLocked(){return locked;} public void setLocked(boolean locked){this.locked=locked;} public boolean isEnabled(){return enabled;} public void setEnabled(boolean enabled){this.enabled=enabled;}
}
