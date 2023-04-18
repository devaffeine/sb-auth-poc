import { Service } from 'typedi';
import jwt, { JwtPayload } from 'jsonwebtoken';

import { AuthResponse } from '../models';
import config from '../config';

export const jwtServiceID = 'JWT_SERVICE';

export interface JwtService {
  createToken(subject: string): AuthResponse;

  parseTokenSubject(token: string): string;
}

@Service({ id: jwtServiceID })
class JwtServiceImpl implements JwtService {
  createToken(subject: string): AuthResponse {
    const expiresInSeconds = config.jwtDaysExpiration * 24 * 60 * 60;
    const token = jwt.sign({ _id: subject }, config.jwtKey, {
      expiresIn: expiresInSeconds,
      subject: subject,
      issuer: config.jwtIssuer,
    });
    var expiresAt = new Date();
    expiresAt.setSeconds(expiresAt.getSeconds() + expiresInSeconds);
    return new AuthResponse(token, 'Bearer', expiresAt);
  }

  parseTokenSubject(token: string): string {
    const decoded = jwt.verify(token, config.jwtKey, {
      issuer: config.jwtIssuer,
      ignoreExpiration: false,
    });
    if (!decoded) {
      throw new Error();
    }
    return (decoded as JwtPayload).sub as string;
  }
}
