# forgerock-auth

Capacitor plugin to integrate ForgeRock Mobile SDK

## Install

```bash
npm install forgerock-auth
npx cap sync
```

## API

<docgen-index>

* [`initialize(...)`](#initialize)
* [`authenticate(...)`](#authenticate)
* [`logout()`](#logout)
* [`enrollBiometrics(...)`](#enrollbiometrics)
* [`authenticateBiometrics(...)`](#authenticatebiometrics)

</docgen-index>

<docgen-api>
<!--Update the source file JSDoc comments and rerun docgen to update the docs below-->

### initialize(...)

```typescript
initialize(options: { url: string; realm: string; journey: string; }) => Promise<void>
```

| Param         | Type                                                          |
| ------------- | ------------------------------------------------------------- |
| **`options`** | <code>{ url: string; realm: string; journey: string; }</code> |

--------------------


### authenticate(...)

```typescript
authenticate(options: { journey: string; username?: string; password?: string; }) => Promise<{ authId?: string; token?: string; userExists?: boolean; }>
```

| Param         | Type                                                                    |
| ------------- | ----------------------------------------------------------------------- |
| **`options`** | <code>{ journey: string; username?: string; password?: string; }</code> |

**Returns:** <code>Promise&lt;{ authId?: string; token?: string; userExists?: boolean; }&gt;</code>

--------------------


### logout()

```typescript
logout() => Promise<{ message: string; }>
```

**Returns:** <code>Promise&lt;{ message: string; }&gt;</code>

--------------------


### enrollBiometrics(...)

```typescript
enrollBiometrics(options: { journey: string; username: string; deviceName: string; }) => Promise<{ success: boolean; message: string; }>
```

| Param         | Type                                                                    |
| ------------- | ----------------------------------------------------------------------- |
| **`options`** | <code>{ journey: string; username: string; deviceName: string; }</code> |

**Returns:** <code>Promise&lt;{ success: boolean; message: string; }&gt;</code>

--------------------


### authenticateBiometrics(...)

```typescript
authenticateBiometrics(options: { journey: string; username: string; }) => Promise<{ token: string; userExists: boolean; authId: string; success: boolean; message: string; }>
```

| Param         | Type                                                |
| ------------- | --------------------------------------------------- |
| **`options`** | <code>{ journey: string; username: string; }</code> |

**Returns:** <code>Promise&lt;{ token: string; userExists: boolean; authId: string; success: boolean; message: string; }&gt;</code>

--------------------

</docgen-api>
