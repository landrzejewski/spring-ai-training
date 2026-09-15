import { StreamableHTTPServerTransport } from "@modelcontextprotocol/sdk/server/streamableHttp.js";
import { createMcpExpressApp } from "@modelcontextprotocol/sdk/server/express.js";
import { buildServer } from "./server.js";

const PORT = Number(process.env.PORT ?? 3000);
const app = createMcpExpressApp();

// Tryb bezstanowy: każde żądanie dostaje świeżą parę server + transport.
app.post("/mcp", async (req, res) => {
    const server = buildServer();
    const transport = new StreamableHTTPServerTransport({ sessionIdGenerator: undefined });
    res.on("close", () => {
        transport.close();
        server.close();
    });
    await server.connect(transport);
    await transport.handleRequest(req, res, req.body);
});

app.listen(PORT, () => {
    console.log(`web-search MCP server running on http://localhost:${PORT}/mcp`);
});
