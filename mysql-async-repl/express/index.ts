import 'reflect-metadata';
import { Container } from 'typedi';
import express, { Express, Request, Response } from 'express';

import config from './src/config';
import { DbService, dbServiceID } from './src/service/db_service';
import { setupAuthApi } from './src/auth_api';

const app: Express = express();
app.use(express.json());

const dbService = Container.get<DbService>(dbServiceID);

app.get('/', (_req: Request, res: Response) => {
  res.send('Auth PoC Server with Express + TypeScript');
});

setupAuthApi(app);

dbService
  .init()
  .then(() => {
    app.listen(config.serverPort, () => {
      console.log(`⚡️[server]: Server is running at port ${config.serverPort}`);
    });
  });
