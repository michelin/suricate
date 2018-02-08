const webpack = require('webpack');
const webpackMerge = require('webpack-merge');
const ExtractTextPlugin = require('extract-text-webpack-plugin');
const helpers = require('./webpack.utils');

const commonConfig = require('./webpack.commons.js');

const ENV = 'dev';

module.exports = webpackMerge(commonConfig({ env: ENV }), {
    devtool: 'eval-source-map',
    output: {
        path: helpers.root('dist'),
        publicPath: '/',
        filename: '[name].js',
        chunkFilename: '[id].chunk.js'
    },

    plugins: [
        new ExtractTextPlugin('[name].css')
    ],
    devServer: {
        historyApiFallback: true,
        stats: 'minimal'
    }
});