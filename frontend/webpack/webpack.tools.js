const autoprefixer = require('autoprefixer');
const postcssUrl = require('postcss-url');
const cssnano = require('cssnano');
const customProperties = require('postcss-custom-properties');

const minimizeCss = false;
const baseHref = "";
const deployUrl = "";
const projectRoot = "d:\\work\\suricate-io\\github\\frontend";

exports.postcssPlugins = function () {
    // safe settings based on: https://github.com/ben-eb/cssnano/issues/358#issuecomment-283696193
    const importantCommentRe = /@preserve|@licen[cs]e|[@#]\s*source(?:Mapping)?URL|^!/i;
    const minimizeOptions = {
        autoprefixer: false,
        safe: true,
        mergeLonghand: false,
        discardComments: { remove: (comment) => !importantCommentRe.test(comment) }
};
    return [
        postcssUrl({
            filter: ({ url }) => url.startsWith('~'),
            url: ({ url }) => path.join(projectRoot, 'node_modules', url.substr(1)),
}),
    postcssUrl([
        {
            // Only convert root relative URLs, which CSS-Loader won't process into require().
            filter: ({ url }) => url.startsWith('/') && !url.startsWith('//'),
        url: ({ url }) => {
        if (deployUrl.match(/:\/\//) || deployUrl.startsWith('/')) {
            // If deployUrl is absolute or root relative, ignore baseHref & use deployUrl as is.
            return `${deployUrl.replace(/\/$/, '')}${url}`;
        }
        else if (baseHref.match(/:\/\//)) {
            // If baseHref contains a scheme, include it as is.
            return baseHref.replace(/\/$/, '') +
                `/${deployUrl}/${url}`.replace(/\/\/+/g, '/');
        }
        else {
            // Join together base-href, deploy-url and the original URL.
            // Also dedupe multiple slashes into single ones.
            return `/${baseHref}/${deployUrl}/${url}`.replace(/\/\/+/g, '/');
        }
    }
},
    {
        // TODO: inline .cur if not supporting IE (use browserslist to check)
        filter: (asset) => !asset.hash && !asset.absolutePath.endsWith('.cur'),
        url: 'inline',
        // NOTE: maxSize is in KB
        maxSize: 10
    }
]),
    autoprefixer(),
        customProperties({ preserve: true })
].concat(minimizeCss ? [cssnano(minimizeOptions)] : []);
};
