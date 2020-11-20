import * as React from 'react';

import { contestFileAPI } from '../../modules/api/uriel/contestFile';

import { HtmlText } from '../HtmlText/HtmlText';

export function FormattedContent({ context, children }) {
  let res = children;
  res = formatDownloadUrls(res, context);

  return <HtmlText>{res}</HtmlText>;
}

function formatDownloadUrls(text, context) {
  const { contestJid } = context;
  if (!contestJid) {
    return text;
  }
  return text.replace(/(src|href)="download\//g, `$1="${contestFileAPI.renderDownloadFilesUrl(contestJid)}/`);
}
