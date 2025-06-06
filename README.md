# forgerock-auth

Capacitor plugin to integrate ForgeRock Mobile SDK

## Install

```bash
npm install forgerock-auth
npx cap sync
```

## API

<docgen-index>

* [`echo(...)`](#echo)
* [`initialize(...)`](#initialize)
* [`authenticate(...)`](#authenticate)
* [`logout()`](#logout)

</docgen-index>

<docgen-api>
<!--Update the source file JSDoc comments and rerun docgen to update the docs below-->

### echo(...)

```typescript
echo(options: { value: string; }) => Promise<{ value: string; }>
```

| Param         | Type                            |
| ------------- | ------------------------------- |
| **`options`** | <code>{ value: string; }</code> |

**Returns:** <code>Promise&lt;{ value: string; }&gt;</code>

--------------------


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

</docgen-api>
