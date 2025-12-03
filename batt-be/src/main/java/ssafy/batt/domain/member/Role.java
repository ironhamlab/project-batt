package ssafy.batt.domain.member;

public enum Role {
  USER, ADMIN;

  public static String toAuthority(Role role) {
    if (role == null) {
      throw new IllegalArgumentException("역할이 정해지지 않았습니다.");
    }
    return "ROLE_" + role.name();
  }
}