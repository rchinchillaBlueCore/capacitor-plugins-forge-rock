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
* [`authenticate()`](#authenticate)
* [`echo(...)`](#echo)

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


### authenticate()

```typescript
authenticate() => Promise<any>
```

**Returns:** <code>Promise&lt;any&gt;</code>

--------------------


### echo(...)

```typescript
echo(options: { value: string; }) => Promise<{ value: string; }>
```

| Param         | Type                            |
| ------------- | ------------------------------- |
| **`options`** | <code>{ value: string; }</code> |

**Returns:** <code>Promise&lt;{ value: string; }&gt;</code>

--------------------

</docgen-api>
