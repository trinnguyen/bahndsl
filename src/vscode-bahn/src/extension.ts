'use strict';

import * as path from 'path';
import * as os from 'os';

import { Trace } from 'vscode-jsonrpc';
import { window, workspace, ExtensionContext, StatusBarAlignment } from 'vscode';
import { LanguageClient, LanguageClientOptions, ServerOptions, StreamInfo } from 'vscode-languageclient';
import * as net from 'net';
import { BahnConfigUtil } from './bahn-config'


const ServerName = 'Bahn IDE server'

const statusBarItem = window.createStatusBarItem(StatusBarAlignment.Right, 0)

export function activate(context: ExtensionContext) {
    // start LC
    var languageClient = null;
    const config = BahnConfigUtil.loadConfig()
    if (config.remoteLspEnabled) {
        console.log('connect to remote LSP server, port: ' + config.remoteLspPort)
        languageClient = createRemoteClient(config.remoteLspPort)    
    } else {
        console.log('launch embedded LSP server')
        languageClient = createEmbeddedClient(context)
    }

    languageClient.trace = Trace.Verbose;
    updateStatusBar(`$(loading) Starting ${ServerName}...`);
    languageClient.onReady().then(() => {
        updateStatusBar(`$(pass) ${ServerName}`);
    });

    // keep disposable for deactivating
    context.subscriptions.push(languageClient.start());
    context.subscriptions.push(statusBarItem);
}

function updateStatusBar(text: string) {
    statusBarItem.text = text;
    statusBarItem.show();
}

/**
 * create LanguageClient that works with embedded BahnDSL language server binary
 * @param context context
 * @returns lc
 */
function createEmbeddedClient(context: ExtensionContext): LanguageClient {
    let launcher = os.platform() === 'win32' ? 'bahn-ide-server.bat' : 'bahn-ide-server';
    let script = context.asAbsolutePath(path.join('bahn-ide-server', 'bin', launcher));

    let serverOptions: ServerOptions = {
        run : { command: script },
        debug: { command: script, args: ['-log', '-trace'] }
    };

    return new LanguageClient(ServerName, serverOptions, createClientOptions());
}

/**
 * create LanguageClient that works with remote LSP (used for debugging)
 * @param port port
 * @returns lc
 */
function createRemoteClient(port: number): LanguageClient {

    const serverInfo = () => {
        const socket = net.connect({ port: port })
        const result: StreamInfo = {
            writer: socket,
            reader: socket
        }
        return Promise.resolve(result)
    }

    return new LanguageClient(ServerName, serverInfo, createClientOptions())
}

/**
 * create client options
 * @returns options
 */
function createClientOptions(): LanguageClientOptions {
    return {
        documentSelector: [{
            scheme: 'file',
            language: 'bahn'
        }]
    }
}