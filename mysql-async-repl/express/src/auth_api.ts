import { Express, NextFunction, Request, Response } from 'express';
import Container from 'typedi';

import { AuthService, authServiceID } from './service/auth_service';
import { AuthRequest, User, UserRequest } from './models';

interface AppRequest extends Request {
  user?: User;
}

export const setupAuthApi = (app: Express) => {
  const authService = Container.get<AuthService>(authServiceID);

  app.post('/sign-up', (req: Request, res: Response) => {
    let userRequest: UserRequest = Object.assign(new UserRequest(), req.body);
    authService
      .signUp(userRequest)
      .then((authResponse) => {
        res.status(201).json(authResponse);
      })
      .catch((_) => {
        res.status(409).json({ message: 'Username already exists' });
      });
  });

  app.post('/sign-in', (req: Request, res: Response) => {
    let authRequest: AuthRequest = Object.assign(new AuthRequest(), req.body);
    authService
      .signIn(authRequest)
      .then((authResponse) => {
        res.status(200).json(authResponse);
      })
      .catch((_) => {
        res.status(401).json({ message: 'Invalid credentials' });
      });
  });

  const auth = async (req: Request, res: Response, next: NextFunction) => {
    try {
      const token = req.header('Authorization')?.replace('Bearer ', '');
      if (token) {
        const user = await authService.parseUserFromAuthToken(token);
        (req as AppRequest).user = user;
        next();
      } else {
        res.status(401).json({ message: 'Please authenticate' });
      }
    } catch (e) {
      res.status(401).json({ message: 'Please authenticate' });
    }
  };

  app.get('/me', auth, (req: AppRequest, res: Response) => {
    res.status(200).json(req.user);
  });
};
