import { WebPlugin } from '@capacitor/core';
import { ForgeRockAuthPlugin } from './definitions';

export class ForgeRockAuthWeb extends WebPlugin implements ForgeRockAuthPlugin {
  async initialize(options: { url: string; realm: string; journey: string }): Promise<void> {
    console.log('SDK inicializado en web con:', options);
  }

  async authenticate(): Promise<any> {
    return { success: true };
  }

  async echo(options: { value: string }): Promise<{
    value: string;
    message: string;
    numeroAleatorio: number;
  }> {
    console.log('HOLA (web)', options);
    return {
      value: options.value,
      message: '¡Hola desde Web!',
      numeroAleatorio: Math.floor(Math.random() * 100),
    };
  }
}
