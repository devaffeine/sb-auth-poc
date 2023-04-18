import dotenv from 'dotenv';

dotenv.config();

interface Config {
  serverPort: number,
  jwtKey: string,
  jwtDaysExpiration: number,
  jwtIssuer: string,
  dbDialect: string,
  dbHost: string,
  dbReadPort: number,
  dbWritePort: number,
  dbDatabase: string,
  dbUser: string,
  dbPassword: string,
}

const config: Config = {
  serverPort: Number(process.env.SERVER_PORT) || 8080,
  jwtKey: process.env.JWT_SECRET_KEY || '',
  jwtDaysExpiration: Number(process.env.JWT_DAYS_EXPIRATION) || 20,
  jwtIssuer: process.env.JWT_ISSUER || 'devaffeine',
  dbDialect: process.env.DB_DIALECT || 'mysql',
  dbHost: process.env.DB_HOST || 'localhost',
  dbReadPort: Number(process.env.DB_READ_PORT) || 3306,
  dbWritePort: Number(process.env.DB_WRITE_PORT) || 3306,
  dbDatabase: process.env.DB_DATABASE || '',
  dbUser: process.env.DB_USER || '',
  dbPassword: process.env.DB_PASSWORD || '',
};

export default config;
