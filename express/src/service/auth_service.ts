import { Inject, Service } from 'typedi';

import { AuthRequest, AuthResponse, User, UserRequest } from "../models";
import { JwtService, jwtServiceID } from './jwt_service';
import { DbService, dbServiceID } from './db_service';

export const authServiceID = 'AUTH_SERVICE';

export interface AuthService {
  signUp(user: UserRequest): Promise<AuthResponse>;

  signIn(auth: AuthRequest): Promise<AuthResponse>;

  parseUserFromAuthToken(token: string): Promise<User>;
}

@Service({ id: authServiceID })
class AuthServiceImpl implements AuthService {
  jwtService: JwtService;
  dbService: DbService;

  constructor(@Inject(jwtServiceID) jwtService: JwtService, @Inject(dbServiceID) dbService: DbService) {
    this.jwtService = jwtService;
    this.dbService = dbService;
  }

  signUp(request: UserRequest): Promise<AuthResponse> {
    return new Promise<AuthResponse>(async (resolve, reject) => {
      const exists = await this.dbService.userExists(request.username);
      if (!exists) {
        const user = await this.dbService.saveUser(request);
        const response = this.jwtService.createToken(user.username);
        return resolve(response);
      }
      reject();
    });
  }

  signIn(auth: AuthRequest): Promise<AuthResponse> {
    return new Promise<AuthResponse>(async (resolve, reject) => {
      try {
        const user = await this.dbService.findUser(auth.username, auth.password);
        if (user) {
          const response = this.jwtService.createToken(user.username);
          return resolve(response);
        }
      } catch (e) {
      }
      reject();
    });
  }

  parseUserFromAuthToken(token: string): Promise<User> {
    return new Promise<User>(async (resolve, reject) => {
      try {
        const username = this.jwtService.parseTokenSubject(token);
        if (username) {
          const user = await this.dbService.findUser(username);
          if (user) {
            return resolve(new User(username, username));
          }
        }
      } catch (e) {
      }
      reject();
    });
  }
}
