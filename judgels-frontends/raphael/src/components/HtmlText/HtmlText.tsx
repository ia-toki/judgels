import HTMLReactParser from 'html-react-parser';
import * as React from 'react';

// CSS definition is in index.scss. See https://github.com/webpack-contrib/mini-css-extract-plugin/issues/250

export const HtmlText = props => <div className="html-text">{HTMLReactParser(props.children)}</div>;
