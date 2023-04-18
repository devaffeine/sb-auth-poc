import { Service } from 'typedi';
import { DataTypes, Dialect, Model, Sequelize } from 'sequelize'

import config from '../config';
import { UserAttributes } from '../models';

export const dbServiceID = 'DB_SERVICE';

export interface DbService {
  init(): Promise<void>;

  userExists(username: string): Promise<boolean>;

  findUser(username: string, password?: string): Promise<UserAttributes | null>;

  saveUser(request: UserAttributes): Promise<UserAttributes>;
}

class UserEntity extends Model implements UserAttributes {
  readonly id?: number
  name!: string
  username!: string
  password!: string
}

@Service({ id: dbServiceID })
class DbServiceImpl implements DbService {
  sequelize: Sequelize;

  constructor() {
    this.sequelize = new Sequelize({
      dialect: config.dbDialect as Dialect,
      replication: {
        read: [{
          host: config.dbHost,
          port: config.dbReadPort,
          database: config.dbDatabase,
          username: config.dbUser,
          password: config.dbPassword,
        }],
        write: {
          host: config.dbHost,
          port: config.dbWritePort,
          database: config.dbDatabase,
          username: config.dbUser,
          password: config.dbPassword,
        },
      },
      pool: {
        max: 50,
      },
    });
  }

  init(): Promise<void> {
    return new Promise<void>(async (resolve) => {
      await this.sequelize.authenticate();

      UserEntity.init({
        id: {
          type: DataTypes.INTEGER,
          autoIncrement: true,
          primaryKey: true
        },
        name: {
          type: DataTypes.STRING,
          allowNull: false,
        },
        username: {
          type: DataTypes.STRING,
          allowNull: false,
        },
        password: {
          type: DataTypes.STRING,
          allowNull: false,
        },
      }, {
        tableName: 'users',
        indexes: [{ unique: true, fields: ['username'] }],
        sequelize: this.sequelize,
      });

      await this.sequelize.sync();
      await this.sequelize.validate();
      resolve();
    });
  }

  userExists(username: string): Promise<boolean> {
    return new Promise<boolean>(async (resolve) => {
      const count = await UserEntity.count({
        where: {
          username: username,
        },
      });
      resolve(count > 0);
    });
  }

  findUser(username: string, password?: string): Promise<UserAttributes | null> {
    return new Promise<UserAttributes>(async (resolve, reject) => {
      const user = await UserEntity.findOne({
        where: {
          username: username,
        },
      });
      if (user && (!password || password == user.password)) {
        resolve(user as UserAttributes);
      }
      reject();
    });
  }

  saveUser(request: UserAttributes): Promise<UserAttributes> {
    return new Promise<UserAttributes>(async (resolve) => {
      let user = await UserEntity.findOne({
        where: {
          username: request.username,
        },
      });
      if (!user) {
        user = UserEntity.build();
      }
      Object.assign(user, request);
      await user.save();
      resolve(user);
    });
  }
}
