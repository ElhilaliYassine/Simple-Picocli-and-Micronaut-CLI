package com.example.search;

import com.example.api.Question;
import com.example.api.SearchHttpRequest;
import com.example.api.StackOverflowHttpClient;
import jakarta.inject.Inject;
import picocli.CommandLine.Help.Ansi;
import picocli.CommandLine.Command;
import picocli.CommandLine.Option;

@Command(name = "search",
        description = "Search questions matching criteria",
        mixinStandardHelpOptions = true)
final public class SearchCommand implements Runnable {

    @Option(names = {"-q", "--query"}, description = "Search phrase")
    String query = "";

    @Option(names = {"-t", "--tag"}, description = "Search inside specific tags")
    String tag = "";

    @Option(names = {"-n", "--limit"}, description = "Limit results. Default: 10")
    int limit = 10;

    @Option(names = {"-s", "--sort-by"}, description = "Available values: relevance, votes, creation, activity. Default: relevance")
    String sort = "relevance";

    @Option(names = {"--verbose"}, description = "Print verbose output")
    boolean verbose;

    @Inject
    StackOverflowHttpClient client;

    @Inject
    SearchHttpRequest request;

    @Override
    public void run() {
        //var response = client.search(query, tag, limit, sort);
        var response = request.execute(query, tag, limit, sort);
        response
                .items
                .stream()
                .map(SearchCommand::formatQuestion)
                .forEach(System.out::println);
        if (verbose) {
            System.out.printf(
                    "\nItems size: %d | Quota max: %d | Quota remaining: %d | Has more: %s\n",
                    response.items.size(),
                    response.quotaMax,
                    response.quotaRemaining,
                    response.hasMore
            );
        }
    }

    public static String formatQuestion(final Question question) {
        return Ansi.AUTO.string(String.format(
                "@|bold,fg(green) %s|@ %d|%d @|bold,fg(yellow) %s|@\n      %s",
                question.accepted ? "✓" : "",
                question.score,
                question.answers,
                question.title,
                question.link
        ));
    }

}
