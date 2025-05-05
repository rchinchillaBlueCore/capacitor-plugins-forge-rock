import { WebPlugin } from '@capacitor/core';
import { ForgeRockAuthPlugin } from './definitions';

export class ForgeRockAuthWeb extends WebPlugin implements ForgeRockAuthPlugin {
  async initialize(options: { url: string; realm: string; journey: string }): Promise<void> {
    console.log('SDK inicializado en web con:', options);
  }

  async authenticate(): Promise<any> {
    return { success: true };
  }
  
  async logout(): Promise<any> {
    return { success: true };
  }

  async enrollBiometrics(): Promise<any> {
    return { success: true };
  }

  async authenticateBiometrics(): Promise<any> {
    return { success: true };
  }

}
