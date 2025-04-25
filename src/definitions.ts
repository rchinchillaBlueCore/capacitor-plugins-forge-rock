import { registerPlugin } from '@capacitor/core';

export interface ForgeRockAuthPlugin {
  echo(options: { value: string }): Promise<{ value: string }>;
  initialize(options: { url: string; realm: string; journey: string }): Promise<void>;
  authenticate(options: {
    journey: string;
    username?: string;
    password?: string;
  }): Promise<{
    authId?: string;
    token?: string;
    userExists?: boolean;
  }>;
  logout(): Promise< {
    message: string;
  }>;

}

const ForgeRockAuth = registerPlugin<ForgeRockAuthPlugin>('ForgeRockAuth');

export default ForgeRockAuth;
