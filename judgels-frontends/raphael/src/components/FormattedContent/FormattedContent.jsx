import * as React from 'react';

import { contestFileAPI } from '../../modules/api/uriel/contestFile';

import { HtmlText } from '../HtmlText/HtmlText';

export interface FormattedContentProps {
  context: any;
  children: any;
}

export const FormattedContent = (props: FormattedContentProps) => {
  let res = props.children as string;
  res = formatDownloadUrls(res, props.context);

  return <HtmlText>{res}</HtmlText>;
};

function formatDownloadUrls(text: string, context: any): string {
  const { contestJid } = context;
  if (!contestJid) {
    return text;
  }
  return text.replace(/(src|href)="download\//g, `$1="${contestFileAPI.renderDownloadFilesUrl(contestJid)}/`);
}
