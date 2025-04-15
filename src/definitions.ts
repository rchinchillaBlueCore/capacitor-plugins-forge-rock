import { registerPlugin } from '@capacitor/core';

export interface ForgeRockAuthPlugin {
  echo(options: { value: string }): Promise<{ value: string }>;
  initialize(options: { url: string; realm: string; journey: string }): Promise<void>;
  authenticate(): Promise<{ success: boolean }>;
}

const ForgeRockAuth = registerPlugin<ForgeRockAuthPlugin>('ForgeRockAuth');

export default ForgeRockAuth;
