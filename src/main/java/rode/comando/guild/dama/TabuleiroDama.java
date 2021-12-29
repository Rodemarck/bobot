package rode.comando.guild.dama;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Member;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import rode.core.Anotacoes.EComandoJogo;
import rode.model.ComandoGuild;
import rode.core.EventLoop;
import rode.core.Helper;
import rode.model.Dama;
import rode.model.Ponto;
import rode.model.maker.MensagemTexto;
import rode.utilitarios.Constantes;

import java.util.Arrays;
import java.util.HashMap;
import java.util.regex.Pattern;

@EComandoJogo
public class TabuleiroDama extends ComandoGuild {
    public TabuleiroDama() {
        super(/*"Dama"*/ null, null, "dama");
    }

    @Override
    public void execute(String[] args, Helper.Mensagem hm) throws Exception {
        var size = hm.getMensagem().getMentionedMembers().size();
        if (size == 1) {
            var mensao = hm.getMensagem().getMentionedMembers().get(0);
            if(mensao.equals(hm.getMembro())){
                hm.reply(hm.getText("dama.so"));
                return;
            }
            if(mensao.getUser().isBot()){
                hm.reply(hm.getText("dama.bot"));
            }
            hm.reply(Constantes.builder(hm.getBundle()), msg -> {
                hm.setMensagem(msg);
                EventLoop.addTexto( new JogoDama(hm,mensao));
            });
        } else if (size > 1) {
            hm.reply(hm.getText("dama.muitos"));
        } else {
            hm.reply(hm.getText("dama.so"));
        }
    }

    public static class JogoDama extends MensagemTexto {
        private static final Logger log = LoggerFactory.getLogger(JogoDama.class);

        private static final HashMap<Character,Integer> VALOR = new HashMap<>(){{
            put('a',0);put('A',0);put('8',0);
            put('b',1);put('B',1);put('7',1);
            put('c',2);put('C',2);put('6',2);
            put('d',3);put('D',3);put('5',3);
            put('e',4);put('E',4);put('4',4);
            put('f',5);put('F',5);put('3',5);
            put('g',6);put('G',6);put('2',6);
            put('h',7);put('H',7);put('1',7);
        }};
        private Dama dama;
        private boolean mudou;
        private EmbedBuilder eb;
        private Member branco;
        private Member preto;
        private boolean vezBranco;
        public JogoDama(Helper.Mensagem hm, Member preto) {
            super(hm, Arrays.asList(hm.getId(),preto.getId()), hm.getText("dama.end"));
            this.branco = hm.getMembro();
            this.preto = preto;
            this.dama = new Dama();
            this.eb = Constantes.builder();
            vezBranco = true;
            setComandos(new HashMap<>() {{
                put(Pattern.compile("^(-?dama\\s*)?[a-hA-H][1-8](\\s+[a-hA-H][1-8])+$"),hm-> type(hm));
                put(Pattern.compile(Constantes.REGEX_SAIR),hm->end());
            }});
            eb.setTitle(hm.getText("dama.title"))
                    .setFooter(Constantes.emote("branco")+branco.getEffectiveName() + "," + Constantes.emote("preto") + preto.getEffectiveName());
            hm.embed(dama.plot(),str->vez(eb.setImage(str),hm));
        }
        private void end(){
            log.info("finalizando");
            finaliza();
        }
        private void type(Helper.Mensagem hm) {
            log.info("tipe");
            if(vezBranco && !hm.getMembro().equals(branco) || !vezBranco && ! hm.getMembro().equals(preto)) {
                hm.reply(hm.getText("dama.vez"));
                return;
            }
            var msg = hm.getMensagem().getContentDisplay();
            log.info("msg = " + msg);
            var p = msg.split("\\s+");
            try {
                var pontos = new Ponto[p.length];
                for(var i=0; i<p.length;i++)
                    pontos[i] = new Ponto(VALOR.get(p[i].charAt(0)), VALOR.get(p[i].charAt(1)));
                System.out.println(pontos[0] + "->" + pontos[1]);
                var jogo = this.dama.play(vezBranco,pontos);
                if(jogo) {
                    hm.reply(hm.getText("dama.valido"));
                    vezBranco =! vezBranco;
                    mudou = true;
                }
                else hm.reply(hm.getText("dama.invalido"));
            }catch (Exception e){
                e.printStackTrace();
                hm.reply(hm.getText("dama.invalido"));
            }
        }

        public EmbedBuilder vez(EmbedBuilder builder, Helper.Mensagem hm){
            return builder.clearFields()
                    .addField(hm.getText("dama.nome"),vezBranco?Constantes.emote("branco")+branco.getEffectiveName():
                            Constantes.emote("preto") + preto.getEffectiveName(), false);
        }
        @Override
        public void rerender(Helper.Mensagem hm) {
            if(mudou){
                setFim(getFim()+ getDelay());
                if(vezBranco)
                    hm.embed(dama.plot(),str->vez(eb.setImage(str),hm));
                else
                    hm.embed(dama.plotFlipped(),str->vez(eb.setImage(str),hm));
                mudou = false;
            }
        }

        @Override
        public void acao(Helper.Mensagem hm) {
        }
    }
}
