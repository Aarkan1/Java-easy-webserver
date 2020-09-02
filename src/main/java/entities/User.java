package entities;

public class User {
  private long id;
  private String firstname;
  private String lastname;
  private int age;

  public User() {}

  public User(long id, String firstname, String lastname, int age) {
    this.id = id;
    this.firstname = firstname;
    this.lastname = lastname;
    this.age = age;
  }

  public User(String firstname, String lastname, int age) {
    this.firstname = firstname;
    this.lastname = lastname;
    this.age = age;
  }

  public long getId() {
    return id;
  }

  public void setId(long id) {
    this.id = id;
  }

  public String getFirstname() {
    return firstname;
  }

  public void setFirstname(String firstname) {
    this.firstname = firstname;
  }

  public String getLastname() {
    return lastname;
  }

  public void setLastname(String lastname) {
    this.lastname = lastname;
  }

  public int getAge() {
    return age;
  }

  public void setAge(int age) {
    this.age = age;
  }

  @Override
  public String toString() {
    return "\nUser { " +
            "id=" + id +
            ", firstname='" + firstname + '\'' +
            ", lastname='" + lastname + '\'' +
            ", age='" + age + '\'' +
            " }";
  }
}
