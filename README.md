A TermSuite launcher on [ISTEX](http://www.istex.fr/) documents.

# Command Line

 1. Download last termsuite-istex's [jar](https://search.maven.org/remotecontent?filepath=fr/univ-nantes/termsuite/termsuite-istex/1.1.2/termsuite-istex-1.1.2.jar),
 2. Run istex launcher:

```
$ java -cp termsuite-istex-1.1.2.jar \
      fr.univnantes.termsuite.istex.cli.IstexLauncher \
      -t /path/to/tagger \
      -l en \
      --tsv istex-termino.tsv \
      --doc-id F697EDBD85006E482CD1AC91DE9D40F6C629727A,15101397F055B3A872D495F7405D0A3F3E195E0F
```


#### Selecting documents

Exactly one option in `--doc-id` or `id-file` must be passed.

 - `--id-file FILE`: A file containing the list of ISTEX document ids of the corpus
 - `--doc-id STRING`: The ","-separated list of ids of ISTEX documents

#### Outputting the extracted terminology

At least one option in `--tsv`, `--json`, `--tbx` must be passed.

 - `--json FILE`: Outputs terminology to JSON file
 - `--tbx FILE`: Outputs terminology to TBX file
 - `--tsv FILE`: Outputs terminology to TSV file

#### Other options

Many additional configuration options are available (TSV output configuration, filtering, extraction pipeline configuration, etc). All options available for TermSuite script `TerminoExtractorCLI` are also available with `IstexLauncher`. See official [`TerminoExtractorCLI` documentation](https://termsuite.github.io/documentation/command-line-api/) for details.

# Run with docker

The main advantage of using docker container for termsuite-istex is that you don't
need to install and configure any external tagger anymore.

See [termsuite-istex-docker](https://github.com/termsuite/termsuite-istex-docker) for more information.

# Java API

to come
