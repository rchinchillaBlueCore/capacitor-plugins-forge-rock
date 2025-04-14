import { WebPlugin } from '@capacitor/core';

import type { ForgeRockAuthPlugin } from './definitions';

export class ForgeRockAuthWeb extends WebPlugin implements ForgeRockAuthPlugin {
  async echo(options: { value: string }): Promise<{ value: string }> {
    console.log('ECHO', options);
    return options;
  }
}
