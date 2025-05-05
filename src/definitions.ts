import { registerPlugin } from '@capacitor/core';

export interface ForgeRockAuthPlugin {
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
  enrollBiometrics(options: {
    journey: string;
    username: string;
    deviceName: string;
  }): Promise<{ 
    success: boolean; 
    message: string 
  }>;
  authenticateBiometrics(options: {
    journey: string;
    username: string;
  }): Promise<{
    token: string;
    userExists: boolean;
    authId: string;
    success: boolean; 
    message: string 
  }>;

}

const ForgeRockAuth = registerPlugin<ForgeRockAuthPlugin>('ForgeRockAuth');

export default ForgeRockAuth;
