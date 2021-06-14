'use strict';

const babelJest = require('babel-jest');

module.exports = babelJest.createTransformer({
  presets: [
    [
      require.resolve('babel-preset-react-app'),
      {
        runtime: 'automatic',
        importSource: 'preact',
      },
    ],
  ],
  babelrc: false,
  configFile: false,
});
