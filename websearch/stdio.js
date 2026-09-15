import { StdioServerTransport } from "@modelcontextprotocol/sdk/server/stdio.js";
import { buildServer } from "./server.js";

await buildServer().connect(new StdioServerTransport());
console.error("web-search MCP server running on stdio"); // nie console.log!