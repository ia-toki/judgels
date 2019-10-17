import React from 'react';

declare global {
  interface Window {
    MathJax: any;
  }
}

export class MathjaxWrapper extends React.Component {
  componentDidMount() {
    this.insertScript('MathJax-config', '/var/conf/mathjax-config.js');
    this.insertScript('MathJax-script', '/mathjax/tex-svg.js');
  }

  componentDidUpdate() {
    window.MathJax.typeset();
  }

  private insertScript(id: string, url: string) {
    const script = document.createElement('script');

    script.id = id;
    script.src = process.env.PUBLIC_URL + url;
    script.defer = true;

    document.body.appendChild(script);
  }

  render() {
    return <div className="mathjax-wrapper">{this.props.children}</div>;
  }
}
