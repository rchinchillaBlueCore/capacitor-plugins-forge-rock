import { registerPlugin } from '@capacitor/core';

export interface ForgeRockAuthPlugin {
  initialize(options: { url: string; realm: string; journey: string }): Promise<{ status: string }>;
  authenticate(): Promise<{ success: boolean }>;
  echo(options: { value: string }): Promise<{ value: string; message: string; numeroAleatorio: number }>;
}

const ForgeRockAuth = registerPlugin<ForgeRockAuthPlugin>('ForgeRockAuth');

export default ForgeRockAuth;
