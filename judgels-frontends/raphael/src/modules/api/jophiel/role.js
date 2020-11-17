export interface UserRole {
  jophiel: JophielRole;
  sandalphon?: string;
  uriel?: string;
  jerahmeel?: string;
}

export enum JophielRole {
  Guest = 'GUEST',
  User = 'USER',
  Admin = 'ADMIN',
  Superadmin = 'SUPERADMIN',
}
