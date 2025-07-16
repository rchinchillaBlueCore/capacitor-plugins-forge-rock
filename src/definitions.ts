import { registerPlugin } from '@capacitor/core';

export interface ForgeRockAuthPlugin {
  echo(options: { value: string }): Promise<{ value: string }>;
  initialize(options: {
    url: string;
    realm: string;
    journey: string;
    oauthClientId: string;
    oauthScope: string;
  }): Promise<void>;
  authenticate(options: { journey: string; username?: string; password?: string; isRetry?: boolean }): Promise<{
    authId?: string;
    token?: string;
    userExists?: boolean;
    status?: string;
    errorMessage?: string;
    callbacks: string[];
  }>;
  logout(): Promise<{
    message: string;
  }>;
  userInfo(): Promise<string>;
  getAccessToken(): Promise<string>;
}

const ForgeRockAuth = registerPlugin<ForgeRockAuthPlugin>('ForgeRockAuth');

export default ForgeRockAuth;
