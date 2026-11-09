export default {
  async fetch(request, env) {
    const cors = {
      "Access-Control-Allow-Origin": "*",
      "Access-Control-Allow-Methods": "POST, OPTIONS",
      "Access-Control-Allow-Headers": "Content-Type"
    };

    if (request.method === "OPTIONS") {
      return new Response(null, { headers: cors });
    }

    if (request.method !== "POST") {
      return Response.json(
        { error: "POST required" },
        { status: 405, headers: cors }
      );
    }

    try {
      const { message } = await request.json();

      if (typeof message !== "string" ||
          message.length === 0 ||
          message.length > 4000) {
        return Response.json(
          { error: "Invalid message" },
          { status: 400, headers: cors }
        );
      }

      const response = await fetch(
        "https://generativelanguage.googleapis.com/v1beta/models/gemini-2.5-flash:generateContent?key=" +
        encodeURIComponent(env.GEMINI_API_KEY),
        {
          method: "POST",
          headers: { "Content-Type": "application/json" },
          body: JSON.stringify({
            contents: [{
              parts: [{ text: message }]
            }]
          })
        }
      );

      const data = await response.json();

      if (!response.ok) {
        return Response.json(
          { error: "Gemini API request failed" },
          { status: 502, headers: cors }
        );
      }

      return Response.json({
        reply: data.candidates?.[0]?.content?.parts?.[0]?.text || "No reply"
      }, { headers: cors });

    } catch (error) {
      return Response.json(
        { error: "Server error" },
        { status: 500, headers: cors }
      );
    }
  }
};
