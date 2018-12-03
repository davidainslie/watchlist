# Design

View this page is in a markdown reader/editor e.g

- [Visual Studio Code Markdown Preview Mermaid Support](https://marketplace.visualstudio.com/items?itemName=bierner.markdown-mermaid)

- [Atom markdown preview](https://atom.io/packages/markdown-preview-enhanced)

- [Typora](https://typora.io)

```mermaid
graph TD
  Start --> Stop
```

## Customer Views their Watchlist

```mermaid
sequenceDiagram
  Customer ->> B: Query
  B->> C: Forward query
  Note right of C: Thinking...
  C->> B: Response
  B->> A: Forward response
```



