package rode.comando.guild;

import jdk.jshell.JShell;
import jdk.jshell.Snippet;
import rode.core.Anotacoes.EComandoProgramador;
import rode.core.EventLoop;
import rode.core.Helper;
import rode.model.ComandoGuild;
import rode.model.maker.MensagemTexto;
import rode.utilitarios.Constantes;

import java.util.Arrays;
import java.util.HashMap;
import java.util.regex.Pattern;

@EComandoProgramador
public class Eval extends ComandoGuild {
    public Eval() {
        super("eval", null, "eval", "shell");
    }

    @Override
    public void execute(String[] __, Helper.Mensagem hm) throws Exception {
        hm.reply(Constantes.builder(hm.getBundle()), message -> {
            hm.setMensagem(message);
            EventLoop.addTexto(new ConversaEval(hm));
        });
    }


    private class ConversaEval extends MensagemTexto {
        private JShell shell;

        private ConversaEval(Helper.Mensagem hm) {
            super(hm, Arrays.asList(hm.getId()), hm.getText("eval.exec.close"));
            setComandos(new HashMap<>() {{
                put(Pattern.compile(Constantes.REGEX_SAIR), h -> end());
                put(Pattern.compile("^[^\\-]"), h -> fun(h));
            }});
            setDelay(180000);
            hm.reply(hm.getText("eval.exec.open"));
            this.shell = JShell.create();

        }

        @Override
        public void acao(Helper.Mensagem hm) {

        }

        @Override
        public void rerender(Helper.Mensagem hm) {

        }

        private void fun(Helper.Mensagem hm) {
            if (shell == null)
                return;
            var comando = hm.getMensagem().getContentStripped();
            var res = shell.eval(comando);
            for (var se : res) {
                if (se.status().equals(Snippet.Status.VALID)) {
                    setFim(getFim()+ getDelay());
                    hm.reply(">>> " + se.value());
                } else{
                    System.out.println(se);
                    hm.reply("error :" + se.causeSnippet());
                }
            }
        }

        private void end() {
            shell = null;
            finaliza();
        }
    }

}
