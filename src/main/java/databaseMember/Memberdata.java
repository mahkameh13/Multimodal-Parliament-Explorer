package databaseMember;

import java.util.List;

public class Memberdata {
   private String id;
   private String surname;
   private String lastname;
   private String party;
   private String fraction;
   private List legislaturperiod;
   private long birthday;

   public Memberdata(String id, String surname, String lastname, String party, String fraction, List legislaturperiod, long birthday) {
      this.id = id;
      this.surname = surname;
      this.lastname = lastname;
      this.party = party;
      this.fraction = fraction;
      this.legislaturperiod = legislaturperiod;
      this.birthday = birthday;
   }

   public String getId() {
      return this.id;
   }

   public String getSurname() {
      return this.surname;
   }

   public String getLastname() {
      return this.lastname;
   }

   public String getParty() {
      return this.party;
   }

   public String getFraction() {
      return this.fraction;
   }

   public List legislaturperiod() {
      return this.legislaturperiod;
   }

   public long getBirthday() {
      return this.birthday;
   }
}
