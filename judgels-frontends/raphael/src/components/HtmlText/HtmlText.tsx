import * as HTMLReactParser from 'html-react-parser';
import * as React from 'react';

import './HtmlText.css';

export const HtmlText = props => <div className="html-text">{HTMLReactParser(props.children)}</div>;
