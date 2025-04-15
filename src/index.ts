import { registerPlugin } from '@capacitor/core';
import type { ForgeRockAuthPlugin } from './definitions';

const ForgeRockAuth = registerPlugin<ForgeRockAuthPlugin>('ForgeRockAuth', {
  web: () => import('./web').then((m) => new m.ForgeRockAuthWeb()),
});

export * from './definitions';
export default ForgeRockAuth;
