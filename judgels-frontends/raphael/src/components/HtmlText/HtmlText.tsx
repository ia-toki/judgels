import HTMLReactParser from 'html-react-parser';
import * as React from 'react';

export const HtmlText = props => <div className="html-text">{HTMLReactParser(props.children)}</div>;
