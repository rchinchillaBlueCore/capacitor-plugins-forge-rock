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
authenticate(options: { journey: string; }) => Promise<{ authId: string; }>
```

| Param         | Type                              |
| ------------- | --------------------------------- |
| **`options`** | <code>{ journey: string; }</code> |

**Returns:** <code>Promise&lt;{ authId: string; }&gt;</code>

--------------------

</docgen-api>
