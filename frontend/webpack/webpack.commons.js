const fs = require('fs');
const path = require('path');
const CopyWebpackPlugin = require('copy-webpack-plugin');
const ProgressPlugin = require('webpack/lib/ProgressPlugin');
const ProvidePlugin = require('webpack/lib/ProvidePlugin');
const HtmlWebpackPlugin = require('html-webpack-plugin');
const rxPaths = require('rxjs/_esm5/path-mapping');

const { NoEmitOnErrorsPlugin, SourceMapDevToolPlugin, NamedModulesPlugin } = require('webpack');
const { NamedLazyChunksWebpackPlugin, BaseHrefWebpackPlugin } = require('@angular/cli/plugins/webpack');
const { CommonsChunkPlugin } = require('webpack').optimize;
const { AngularCompilerPlugin } = require('@ngtools/webpack');

const nodeModules = path.join(process.cwd(), 'node_modules');
const realNodeModules = fs.realpathSync(nodeModules);
const genDirNodeModules = path.join(process.cwd(), 'src', '$$_gendir', 'node_modules');
const entryPoints = ["inline","polyfills","sw-register","styles","vendor","main"];

const csstools = require('./webpack.tools');

module.exports = (options) =>
{
    return {
        resolve: {
            extensions: [".ts", ".js"],
            modules: ["./node_modules"],
            symlinks: true,
            alias: rxPaths(),
            mainFields: ["browser", "module", "main"]
        },
        entry: {
            main: ["./src/main"],
            polyfills: ["./src/polyfills"],
            styles: [
                "./src/assets/styles/normalize.css",
                "./src/assets/styles/dashboard-dark-material-theme.scss",
                "./node_modules/material-design-icons-iconfont/dist/material-design-icons.scss",
                "./src/styles.css"
            ]
        },
        output: {
            path: path.join(process.cwd(), "dist"),
            filename: "[name].bundle.js",
            chunkFilename: "[id].chunk.js",
            crossOriginLoading: false
        },
        node: {
            net: 'empty'
        },
        module: {
            rules: [
                {test: /\.html$/, loader: "raw-loader"},
                {
                    test: /\.(eot|svg|cur)$/, loader: "file-loader",
                    options: {name: "[name].[hash:20].[ext]", limit: 10000}
                },
                {
                    test: /\.(jpg|png|webp|gif|otf|ttf|woff|woff2|ani)$/, loader: "url-loader",
                    options: {name: "[name].[hash:20].[ext]", limit: 10000}
                },
                {
                    test: /\.css$/,
                    exclude: [
                        path.join(process.cwd(), "src\\assets\\styles\\normalize.css"),
                        path.join(process.cwd(), "src\\assets\\styles\\dashboard-dark-material-theme.scss"),
                        path.join(process.cwd(), "node_modules\\material-design-icons-iconfont\\dist\\material-design-icons.scss"),
                        path.join(process.cwd(), "src\\styles.css")
                    ],
                    use: [
                        "exports-loader?module.exports.toString()",
                        {
                            loader: "css-loader",
                            options: {sourceMap: false, importLoaders: 1}
                        },
                        {
                            loader: "postcss-loader",
                            options: {ident: "postcss", plugins: csstools.postcssPlugins, sourceMap: false}
                        }
                    ]
                },
                {
                    test: /\.scss$|\.sass$/,
                    exclude: [
                        path.join(process.cwd(), "src\\assets\\styles\\normalize.css"),
                        path.join(process.cwd(), "src\\assets\\styles\\dashboard-dark-material-theme.scss"),
                        path.join(process.cwd(), "node_modules\\material-design-icons-iconfont\\dist\\material-design-icons.scss"),
                        path.join(process.cwd(), "src\\styles.css")
                    ],
                    use: [
                        "exports-loader?module.exports.toString()",
                        {
                            loader: "css-loader",
                            options: {sourceMap: false, importLoaders: 1}
                        },
                        {
                            loader: "postcss-loader",
                            options: {ident: "postcss", plugins: csstools.postcssPlugins, sourceMap: false}
                        },
                        {
                            loader: "sass-loader",
                            options: {sourceMap: false, precision: 8, includePaths: []}
                        }
                    ]
                },
                {
                    test: /\.css$/,
                    include: [
                        path.join(process.cwd(), "src\\assets\\styles\\normalize.css"),
                        path.join(process.cwd(), "src\\assets\\styles\\dashboard-dark-material-theme.scss"),
                        path.join(process.cwd(), "node_modules\\material-design-icons-iconfont\\dist\\material-design-icons.scss"),
                        path.join(process.cwd(), "src\\styles.css")
                    ],
                    use: [
                        "style-loader",
                        {
                            loader: "css-loader",
                            options: {sourceMap: false, importLoaders: 1}
                        },
                        {
                            loader: "postcss-loader",
                            options: {ident: "postcss", plugins: csstools.postcssPlugins, sourceMap: false}
                        }
                    ]
                },
                {
                    test: /\.scss$|\.sass$/,
                    include: [
                        path.join(process.cwd(), "src\\assets\\styles\\normalize.css"),
                        path.join(process.cwd(), "src\\assets\\styles\\dashboard-dark-material-theme.scss"),
                        path.join(process.cwd(), "node_modules\\material-design-icons-iconfont\\dist\\material-design-icons.scss"),
                        path.join(process.cwd(), "src\\styles.css")
                    ],
                    use: [
                        "style-loader",
                        {
                            loader: "css-loader",
                            options: {sourceMap: false, importLoaders: 1}
                        },
                        {
                            loader: "postcss-loader",
                            options: {ident: "postcss", plugins: csstools.postcssPlugins, sourceMap: false}
                        },
                        {
                            loader: "sass-loader",
                            options: {sourceMap: false, precision: 8, includePaths: []}
                        }
                    ]
                },
                {
                    test: /\.ts$/, loader: "@ngtools/webpack"
                }
            ]
        },
        plugins: [
            new NoEmitOnErrorsPlugin(),
            new CopyWebpackPlugin([
                {context: "src", to: "", from: {glob: "assets/**/*", dot: true}},
                {context: "src", to: "", from: {glob: "favicon.ico", dot: true}}
            ], {ignore: [".gitkeep", "**/.DS_Store", "**/Thumbs.db"], debug: "warning"}),
            new ProgressPlugin(),
            new NamedLazyChunksWebpackPlugin(),
            new HtmlWebpackPlugin({
                template: "./src/index.html",
                filename: "./index.html",
                hash: false,
                inject: true,
                compile: true,
                favicon: false,
                minify: false,
                cache: true,
                showErrors: true,
                chunks: "all",
                excludeChunks: [],
                title: "Webpack App",
                xhtml: true,
                chunksSortMode: function sort(left, right) {
                    let leftIndex = entryPoints.indexOf(left.names[0]);
                    let rightindex = entryPoints.indexOf(right.names[0]);
                    if (leftIndex > rightindex) {
                        return 1;
                    }
                    else if (leftIndex < rightindex) {
                        return -1;
                    }
                    else {
                        return 0;
                    }
                }
            }),
            new BaseHrefWebpackPlugin({}),
            new CommonsChunkPlugin({name: ["inline"], minChunks: null}),
            new CommonsChunkPlugin({
                name: ["vendor"],
                minChunks: (module) => {
                    return module.resource
                    && (module.resource.startsWith(nodeModules)
                        || module.resource.startsWith(genDirNodeModules)
                        || module.resource.startsWith(realNodeModules));
                },
                chunks: ["main"]
            }),
            new SourceMapDevToolPlugin({
                filename: "[file].map[query]",
                moduleFilenameTemplate: "[resource-path]",
                fallbackModuleFilenameTemplate: "[resource-path]?[hash]",
                sourceRoot: "webpack:///"
            }),
            new CommonsChunkPlugin({
                name: ["main"], minChunks: 2, async: "common"
            }),
            new NamedModulesPlugin({}),
            new AngularCompilerPlugin({
                mainPath: "main.ts",
                platform: 0,
                hostReplacementPaths: {"environments\\environment.ts": "environments\\environment.ts"},
                sourceMap: true,
                tsConfigPath: "src\\tsconfig.app.json",
                skipCodeGeneration: true,
                compilerOptions: {}
            }),
            new ProvidePlugin({
                $: "jquery",
                jquery: "jquery",
                jQuery: "jquery"
            })
        ],
        devServer: {
            historyApiFallback: true
        }
    };
}