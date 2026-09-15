import { McpServer } from "@modelcontextprotocol/sdk/server/mcp.js";
import { z } from "zod";

async function search(query, maxResults) {
    const res = await fetch("https://api.tavily.com/search", {
        method: "POST",
        headers: {
            "Content-Type": "application/json",
            Authorization: `Bearer ${process.env.TAVILY_API_KEY}`,
        },
        body: JSON.stringify({ query, max_results: maxResults }),
    });
    if (!res.ok) throw new Error(`Search API error: ${res.status}`);
    const data = await res.json();
    return data.results.map(r => ({ title: r.title, url: r.url, snippet: r.content }));
}

export function buildServer() {
    const server = new McpServer({ name: "web-search", version: "1.0.0" });

    server.registerTool(
        "web_search",
        {
            title: "Web search",
            description: "Wyszukuje aktualne informacje w internecie. Zwraca tytuły, URL-e i fragmenty stron.",
            inputSchema: {
                query: z.string().describe("Zapytanie wyszukiwania"),
                maxResults: z.number().int().min(1).max(10).default(5),
            },
        },
        async ({ query, maxResults }) => {
            try {
                const results = await search(query, maxResults);
                return { content: [{ type: "text", text: JSON.stringify(results, null, 2) }] };
            } catch (e) {
                return { content: [{ type: "text", text: e.message }], isError: true };
            }
        }
    );

    return server;
}