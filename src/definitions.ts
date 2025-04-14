export interface ForgeRockAuthPlugin {
  echo(options: { value: string }): Promise<{ value: string }>;
}
