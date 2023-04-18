export interface UserAttributes {
  name: string;
  username: string;
  password?: string;
}

export class UserRequest implements UserAttributes {
  name!: string;
  username!: string;
  password!: string;
}

export class AuthRequest {
  username!: string;
  password!: string;
}

export class AuthResponse {
  token: string;
  type: string;
  expiresAt: Date;

  constructor(token: string, type: string, expiresAt: Date) {
    this.token = token;
    this.type = type;
    this.expiresAt = expiresAt;
  }
}

export class User implements UserAttributes {
  name: string;
  username: string;
  password?: string;

  constructor(name: string, username: string, password?: string) {
    this.name = name;
    this.username = username;
    this.password = password;
  }
}
